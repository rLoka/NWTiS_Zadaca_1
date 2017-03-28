package org.foi.nwtis.kgrlic.zadaca_1;

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
public class KlijentSustava {

    private final ArrayList<String> naredba;

    /**
     *
     * @param naredba
     */
    public KlijentSustava(ArrayList<String> naredba) {
        this.naredba = naredba;
    }

    /**
     * Izvršava klijentsku naredbu
     */
    public void izvrsiKlijentNaredbu() {
        String server = this.naredba.get(2);
        int port = Integer.parseInt(this.naredba.get(3));
        String korisnik = this.naredba.get(4);
        String tip = this.naredba.get(5);
        String vrijednost = this.naredba.get(6);

        if (!this.provjeraParametara(server, port, korisnik, vrijednost)) {
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

            String zahtjev = "USER " + korisnik + "; ";

            switch (tip) {
                case "a":
                    zahtjev += "ADD " + vrijednost + ";";
                    break;
                case "t":
                    zahtjev += "TEST " + vrijednost + ";";
                    break;
                case "w":
                    zahtjev += "WAIT " + vrijednost + ";";
                    break;
            }

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
            System.out.println("Primljeni  odgovor: " + stringBuilder);
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
     * Provjerava ispravnost parametara
     */
    private boolean provjeraParametara(String server, int port, String korisnik, String vrijednost) {

        Validator validator = new Validator();

        boolean provjeraIpAdrese = validator.stringValjan(server, Validator.IPADRESA);
        boolean provjeraNazivaPosluzitelja = validator.stringValjan(server, Validator.POSLUZITELJ);
        boolean provjeraRasponaPorta = validator.rasponValjan(port, Validator.PORT);
        boolean provjeraKorisnickogImena = validator.stringValjan(korisnik, Validator.KORISNICKO_IME);
        boolean provjeraUrla = validator.stringValjan(vrijednost, Validator.URL);
        
        boolean provjeraNnn = false;        
        if(!provjeraUrla && vrijednost.length() <= 3){
            provjeraNnn = validator.rasponValjan(vrijednost, Validator.NNN);
        }       

        if (!provjeraIpAdrese && !provjeraNazivaPosluzitelja) {
            System.out.println("IP adresa/poslužitelj nisu u valjani!");
            return false;
        } else if (!provjeraRasponaPorta) {
            System.out.println("Port nije valjan!");
            return false;
        } else if (!provjeraKorisnickogImena) {
            System.out.println("Korisničko ime nije u zadanom formatu!");
            return false;
        } else if (!provjeraUrla && !provjeraNnn) {
            System.out.println("Url/Broj sekundi nije zadanom formatu!");
            return false;
        }

        return true;
    }

}
