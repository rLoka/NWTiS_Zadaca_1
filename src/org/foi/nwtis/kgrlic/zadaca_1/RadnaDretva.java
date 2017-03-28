package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.kgrlic.konfiguracije.Konfiguracija;

/**
 * Radna dretva
 * @author kgrlic
 */
public class RadnaDretva extends Thread {

    private final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final ArrayList<RadnaDretva> listaAktivnihRadnihDretvi;
    private final long vrijemePocetka;
    private final ServerRuntimeKonfiguracija serverRuntimeKonfiguracija;
    private final Konfiguracija konfiguracija;

    RadnaDretva(Socket socket, ArrayList<RadnaDretva> listaAktivnihRadnihDretvi, short redniBrojDretve, Konfiguracija konfiguracija) {

        //Postavljanje imena dretve
        this.setName("kgrlic-" + Short.toUnsignedInt(redniBrojDretve));
        System.out.println("Pokrenuta dretva: " + this.getName());

        this.socket = socket;
        this.listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi;
        this.serverRuntimeKonfiguracija = ServerRuntimeKonfiguracija.getInstance();
        this.vrijemePocetka = System.currentTimeMillis();
        this.konfiguracija = konfiguracija;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        System.out.println("Izvodenje radne dretve " + this.getName() + " prekinuto!");
        this.zatvoriSocketSaKorisnikom();
        this.ukloniDretvuIzListeAktivnihDretvi();
    }

    @Override
    public void run() {
        try {
            System.out.println(this.getClass());
            //TODO ažurirati evidenciju rada

            try {
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();

                StringBuffer stringBuffer = new StringBuffer();

                while (true) {
                    int znak = inputStream.read();
                    if (znak == -1) {
                        break;
                    }
                    stringBuffer.append((char) znak);
                }

                System.out.println("Primljena naredba: " + stringBuffer);

                ArrayList<String> naredba = this.identificirajNaredbu(stringBuffer);

                if (naredba == null) {
                    this.outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
                } else if ("PASSWD".equals(naredba.get(2))) {
                    if (this.jeLiKorisnikAdmin(naredba.get(1), naredba.get(3))) {
                        this.izvrsiAdminNaredbu(naredba);
                    } else {
                        this.outputStream.write("ERROR 00; Korisnik nema administratorske ovlasti ili lozinka nije točna!".getBytes());
                    }

                } else {
                    if ("PAUSED".equals(this.serverRuntimeKonfiguracija.getStatus())) {
                        this.outputStream.write("ERROR 01; Server je pauziran!".getBytes());
                    } else {
                        switch (naredba.get(2)) {
                            case "ADD":
                                this.izvrsiKlijentAddNaredbu(naredba);
                                break;
                            case "TEST":
                                this.izvrsiKlijentTestNaredbu(naredba);
                                break;
                            case "WAIT":
                                this.izvrsiKlijentWaitNaredbu(naredba);
                                break;
                        }
                    }
                }

                this.outputStream.flush();

            } catch (IOException | NullPointerException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                this.zatvoriSocketSaKorisnikom();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            try {
                this.outputStream.write("ERROR 13; Dretva prekinuta u čekanju!".getBytes());
                this.outputStream.flush();
                this.zatvoriSocketSaKorisnikom();
            } catch (IOException ex1) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Identificira zaprimljene naredbe
     */
    private ArrayList<String> identificirajNaredbu(StringBuffer korisnickaNaredba) {

        Validator validator = new Validator();

        if (validator.stringValjan(korisnickaNaredba, Validator.PASSWD)) {
            return validator.grupe(korisnickaNaredba, Validator.PASSWD);
        } else if (validator.stringValjan(korisnickaNaredba, Validator.ADD)) {
            return validator.grupe(korisnickaNaredba, Validator.ADD);
        } else if (validator.stringValjan(korisnickaNaredba, Validator.TEST)) {
            return validator.grupe(korisnickaNaredba, Validator.TEST);
        } else if (validator.stringValjan(korisnickaNaredba, Validator.WAIT)) {
            return validator.grupe(korisnickaNaredba, Validator.WAIT);
        }

        return null;

    }

    /**
     * Identificira i izvršava naredbe od admina.
     */
    private void izvrsiAdminNaredbu(ArrayList<String> naredba) throws IOException, InterruptedException {
        switch (naredba.get(4)) {
            case "PAUSE":
                this.pauzirajServer();
                break;
            case "START":
                this.startajServer();
                break;
            case "STOP":
                this.stopirajServer();
                break;
            case "STAT":
                this.posaljiEvidencijuKorisniku();
                break;
            default:
                this.outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
        }

    }

    /**
     * Zabranjuje primanje korisničkih naredbi
     */
    private void pauzirajServer() throws IOException {
        switch (this.serverRuntimeKonfiguracija.getStatus()) {
            case "STARTED":
                this.serverRuntimeKonfiguracija.setStatus("PAUSED");
                this.outputStream.write("OK;".getBytes());
                break;
            case "STOPPED":
                this.outputStream.write("ERROR 01; Server završava s radom!".getBytes());
                break;
            default:
                this.outputStream.write("ERROR 01; Server je već pauziran!".getBytes());
                break;
        }
    }

    /**
     * Dopušta primanje korisničkih naredbi
     */
    private void startajServer() throws IOException {
        switch (this.serverRuntimeKonfiguracija.getStatus()) {
            case "PAUSED":
                this.serverRuntimeKonfiguracija.setStatus("STARTED");
                this.outputStream.write("OK;".getBytes());
                break;
            case "STOPPED":
                this.outputStream.write("ERROR 01; Server završava s radom!".getBytes());
                break;
            default:
                this.outputStream.write("ERROR 02; Server je već pokrenut!".getBytes());
                break;
        }
    }

    /**
     * Stopira server gasi JVM
     */
    private void stopirajServer() throws IOException {
        this.serverRuntimeKonfiguracija.setStatus("STOPPED");
        this.outputStream.write("OK;".getBytes());
        System.exit(0);
    }

    /**
     * Izvršava add naredbu
     */
    private void izvrsiKlijentAddNaredbu(ArrayList<String> naredba) throws IOException {
        Evidencija evidencija = Evidencija.getInstance();
        if (Integer.parseInt(this.konfiguracija.dajPostavku("maksAdresa")) >= evidencija.zahtijeviAdrese.size()) {
            outputStream.write("ERROR 10; Nema slobodnog mjesta za adresu!".getBytes());
        } else if (evidencija.zahtijeviAdrese.contains(naredba.get(3))) {
            outputStream.write("ERROR 11; Već postoji ta adresa!".getBytes());
        } else {
            evidencija.zahtijeviAdrese.add(naredba.get(3));
            outputStream.write("OK;".getBytes());
        }
    }

    /**
     * Izvršava test naredbu
     */
    private void izvrsiKlijentTestNaredbu(ArrayList<String> naredba) throws IOException {
        Evidencija evidencija = Evidencija.getInstance();
        if (evidencija.zahtijeviAdrese.contains(naredba.get(3))) {
            outputStream.write("OK; YES!".getBytes());
        } else {
            evidencija.zahtijeviAdrese.add(naredba.get(3));
            outputStream.write("ERROR 13; Ne postoji ta adresa!".getBytes());
        }
    }

    /**
     * Izvršava naredbu za čekanjem
     */
    private void izvrsiKlijentWaitNaredbu(ArrayList<String> naredba) throws IOException, InterruptedException {
        Thread.sleep(Integer.parseInt(naredba.get(3)));
        outputStream.write("OK;".getBytes());
    }

    /**
     * Vraća vrijeme izvođenja trenutne dretve
     *
     * @return
     */
    public long vrijemeIzvodenja() {
        System.out.println("Ovo izvrsava: " + this.getName());
        return System.currentTimeMillis() - this.vrijemePocetka;
    }

    /**
     * Uklanja dretvu iz liste aktivnih dretvi
     */
    public void ukloniDretvuIzListeAktivnihDretvi() {
        this.listaAktivnihRadnihDretvi.remove(this);
    }

    /**
     * Zatvara socket i in/output streamove
     */
    private void zatvoriSocketSaKorisnikom() {
        try {
            if (this.inputStream != null) {
                this.inputStream.close();
            }

            if (this.outputStream != null) {
                this.outputStream.close();
            }
            this.socket.close();
            this.ukloniDretvuIzListeAktivnihDretvi();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Ispituje da li je korisnik administrator
     */
    private boolean jeLiKorisnikAdmin(String korisnik, String lozinka) throws FileNotFoundException, IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.konfiguracija.dajPostavku("adminDatoteka")))) {
            String linija;
            while ((linija = bufferedReader.readLine()) != null) {
                String[] korisnikLozinka = linija.split(";");
                if (korisnikLozinka[0].equals(korisnik) && korisnikLozinka[1].equals(lozinka)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Šalje korisniku evidenciju u obliku niza byteva
     */
    private void posaljiEvidencijuKorisniku() throws IOException {
        byte[] evidencijaBytes = ucitajEvidencijskuDatoteku();
        if (evidencijaBytes != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(("OK; LENGTH " + evidencijaBytes.length + "\r\n").getBytes());
            byteArrayOutputStream.write(evidencijaBytes);
            this.outputStream.write(byteArrayOutputStream.toByteArray());
        }
    }

    /**
     * Učitava evidencijsku datoteku u niz byteva
     */
    private byte[] ucitajEvidencijskuDatoteku() throws IOException {
        Validator validator = new Validator();
        String evidencijskaDatoteka = konfiguracija.dajPostavku("evidDatoteka");

        if (evidencijskaDatoteka.length() == 0 || evidencijskaDatoteka == null) {
            this.outputStream.write("ERROR 04; Evidencijska datoteka nije definirana!".getBytes());
        } else {
            boolean datotekaNaDiskuPostoji = validator.datotekaNaDiskuPostoji(evidencijskaDatoteka);
            if (datotekaNaDiskuPostoji) {
                EvidencijaLoader evidencijaLoader = new EvidencijaLoader();
                return evidencijaLoader.ucitajEvidencijuSaDiskaBytes(evidencijskaDatoteka);
            } else {
                this.outputStream.write("ERROR 04; Evidencijska datoteka ne postoji!".getBytes());
            }
        }
        return null;
    }
}
