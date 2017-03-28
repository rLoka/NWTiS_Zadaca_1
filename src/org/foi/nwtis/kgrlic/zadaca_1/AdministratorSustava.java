package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rloka
 */
public class AdministratorSustava {

    private final ArrayList<String> naredba;

    /**
     *
     * @param naredba
     */
    public AdministratorSustava(ArrayList<String> naredba) {
        this.naredba = naredba;
    }

    /**
     * Izvršava naredbu korisnika koji se identificirao kao administrator
     * @throws ClassNotFoundException
     */
    public void izvrsiAdminNaredbu() throws ClassNotFoundException {
        String server = this.naredba.get(2);
        int port = Integer.parseInt(this.naredba.get(3));
        String korisnik = this.naredba.get(4);
        String lozinka = this.naredba.get(5);
        String komanda = this.naredba.get(6);

        if (!this.provjeraParametara(server, port, korisnik, lozinka)) {
            System.out.println("Proslijeđeni parametri ne odgovaraju! Gasim program ...");
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            String zahtjev = "USER " + korisnik + "; PASSWD " + lozinka + "; " + komanda.toUpperCase() + ";";
            outputStream.write(zahtjev.getBytes());
            outputStream.flush();
            socket.shutdownOutput();

            StringBuffer stringBuilder = new StringBuffer();
            while (true) {
                int znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                stringBuilder.append((char) znak);
            }

            if ("STAT".equals(komanda.toUpperCase())) {
                String odgovor[] = stringBuilder.toString().split("\r\n");
                System.out.println("Primljeni  odgovor: " + odgovor[0]);
                try (FileOutputStream fileOutputStream = new FileOutputStream("tmpPrimljenaEvidencija.bin")) {
                    fileOutputStream.write(odgovor[1].getBytes());
                }
                EvidencijaLoader evidencijaLoader = new EvidencijaLoader();
                Evidencija.setInstance(evidencijaLoader.ucitajEvidencijuSaDiska("tmpPrimljenaEvidencija.bin"));
                evidencijaLoader.ispisiEvidenciju(Evidencija.getInstance());

            } else {
                System.out.println("Primljeni  odgovor: " + stringBuilder);
            }

        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Radi provjeru parametara
     */
    private boolean provjeraParametara(String server, int port, String korisnik, String lozinka) {

        Validator validator = new Validator();

        boolean provjeraIpAdrese = validator.stringValjan(server, Validator.IPADRESA);
        boolean provjeraNazivaPosluzitelja = validator.stringValjan(server, Validator.POSLUZITELJ);
        boolean provjeraRasponaPorta = validator.rasponValjan(port, Validator.PORT);
        boolean provjeraKorisnickogImena = validator.stringValjan(korisnik, Validator.KORISNICKO_IME);
        boolean provjeraLozinke = validator.stringValjan(lozinka, Validator.LOZINKA);

        if (!provjeraIpAdrese && !provjeraNazivaPosluzitelja) {
            System.out.println("IP adresa/poslužitelj nisu u valjani!");
            return false;
        } else if (!provjeraRasponaPorta) {
            System.out.println("Port nije valjan!");
            return false;
        } else if (!provjeraKorisnickogImena) {
            System.out.println("Korisničko ime nije u zadanom formatu!");
            return false;
        } else if (!provjeraLozinke) {
            System.out.println("Lozinka zadanom formatu!");
            return false;
        }

        return true;
    }

}
