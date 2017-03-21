package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author rloka
 */
public class KlijentSustava {

    private final Matcher matcher;

    public KlijentSustava(Matcher matcher) {
        this.matcher = matcher;
    }

    public void izvrsiKlijentNaredbu() {
        String server = this.matcher.group(2);
        int port = Integer.parseInt(this.matcher.group(3));
        String korisnik = this.matcher.group(4);
        String tip = this.matcher.group(5);
        String vrijednost = this.matcher.group(6);

        InputStream inputStream = null;
        OutputStream outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            
            String zahtjev = "USER " + korisnik + "; ";
            
            switch(tip){
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

}
