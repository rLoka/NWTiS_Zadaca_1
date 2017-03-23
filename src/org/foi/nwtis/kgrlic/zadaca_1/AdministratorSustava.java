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
public class AdministratorSustava {
    
    private final ArrayList<String> naredba;

    public AdministratorSustava(ArrayList<String> naredba) {
        this.naredba = naredba;
    }
    
    public void izvrsiAdminNaredbu() {
        String server = this.naredba.get(2);
        int port = Integer.parseInt(this.naredba.get(3));
        String korisnik = this.naredba.get(4);
        String lozinka = this.naredba.get(5);
        String komanda = this.naredba.get(6);

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
    
}
