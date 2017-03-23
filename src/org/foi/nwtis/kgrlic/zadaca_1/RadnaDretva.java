package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kgrlic
 */
public class RadnaDretva extends Thread {

    private final Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private final ArrayList<RadnaDretva> listaAktivnihRadnihDretvi;
    private final ArrayList<String> listaNaredbi;
    private final long vrijemePocetka;
    private final ServerRuntimeKonfiguracija serverRuntimeKonfiguracija;

    RadnaDretva(Socket socket, ArrayList<RadnaDretva> listaAktivnihRadnihDretvi, short redniBrojDretve) {

        //Postavljanje imena dretve
        this.setName("kgrlic-" + Short.toUnsignedInt(redniBrojDretve));
        System.out.println("Pokrenuta dretva: " + this.getName());

        this.socket = socket;
        this.listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi;
        this.listaNaredbi = new ArrayList<>();

        this.listaNaredbi.add("^USER ([^\\s]+); (PASSWD) ([^\\s]+); (PAUSE|STOP|START|STAT);$");
        this.listaNaredbi.add("^USER ([^\\s]+); (ADD) ([^\\s]+);$");
        this.listaNaredbi.add("^USER ([^\\s]+); (TEST) ([^\\s]+);$");
        this.listaNaredbi.add("^USER ([^\\s]+); (WAIT) ([^\\s]+);$");

        this.serverRuntimeKonfiguracija = ServerRuntimeKonfiguracija.getInstance();

        this.vrijemePocetka = System.currentTimeMillis();
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

            System.out.println(this.getClass()); //TODO obrisati dretvu iz kolekcije aktivnih radnih dretvi
            //TODO smanjiti brojač aktivnih radnih dretvi
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

                Matcher zaprimljeneNaredbe = this.identificirajNaredbu(stringBuffer);

                if (zaprimljeneNaredbe == null) {
                    this.outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
                }

                if ("PASSWD".equals(zaprimljeneNaredbe.group(2))) {
                    this.izvrsiAdminNaredbu(zaprimljeneNaredbe);
                } else {
                    if ("PAUSED".equals(this.serverRuntimeKonfiguracija.getStatus())) {
                        this.outputStream.write("ERROR 01; Server je pauziran!".getBytes());
                    } else {
                        switch (zaprimljeneNaredbe.group(2)) {
                            case "ADD":
                                this.izvrsiKlijentAddNaredbu(zaprimljeneNaredbe);
                                break;
                            case "TEST":
                                this.izvrsiKlijentTestNaredbu(zaprimljeneNaredbe);
                                break;
                            case "WAIT":
                                this.izvrsiKlijentWaitNaredbu(zaprimljeneNaredbe);
                                break;
                        }
                    }
                }

                this.outputStream.flush();

            } catch (IOException | NullPointerException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (this.inputStream != null) {
                        this.inputStream.close();
                    }

                    if (this.outputStream != null) {
                        this.outputStream.close();
                    }

                    this.socket.close();

                } catch (IOException ex) {
                    Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            this.ukloniDretvuIzListeAktivnihDretvi();
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private Matcher identificirajNaredbu(StringBuffer korisnickaNaredba) {

        for (String naredba : this.listaNaredbi) {
            Pattern pattern = Pattern.compile(naredba);
            Matcher matcher = pattern.matcher(korisnickaNaredba);

            if (matcher.matches()) {
                return matcher;
            }
        }

        return null;
    }

    private void izvrsiAdminNaredbu(Matcher zaprimljeneNaredbe) throws IOException, InterruptedException {
        switch (zaprimljeneNaredbe.group(4)) {
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
                outputStream.write("OK; STAT".getBytes());
                break;
            default:
                outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
        }

    }

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

    private void stopirajServer() throws IOException {
        this.serverRuntimeKonfiguracija.setStatus("STOPPED");
        this.outputStream.write("OK;".getBytes());
        System.exit(0);
    }

    private void izvrsiKlijentAddNaredbu(Matcher matcher) throws IOException {
        outputStream.write("OK; ADD".getBytes());
    }

    private void izvrsiKlijentTestNaredbu(Matcher matcher) throws IOException {
        outputStream.write("OK; TEST".getBytes());
    }

    private void izvrsiKlijentWaitNaredbu(Matcher matcher) throws IOException {
        outputStream.write("OK; WAIT".getBytes());
    }

    public long vrijemeIzvodenja() {
        System.out.println("Ovo izvrsava: " + this.getName());
        return System.currentTimeMillis() - this.vrijemePocetka;
    }

    public void ukloniDretvuIzListeAktivnihDretvi() {
        this.listaAktivnihRadnihDretvi.remove(this);
    }

    private void zatvoriSocketSaKorisnikom() {
        try {
            this.socket.close();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
