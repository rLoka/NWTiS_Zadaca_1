/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.kgrlic.konfiguracije.Konfiguracija;

/**
 *
 * @author kgrlic
 */
public class RezervnaDretva extends Thread {

    Konfiguracija konfiguracija;
    Socket socket;

    /**
     *
     * @param konfiguracija
     */
    public RezervnaDretva(Konfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        int trajanjeSpavanja = Integer.parseInt(konfiguracija.dajPostavku("intervalAdresneDretve"));

        while (true) {
            System.out.println(this.getClass());
            long trenutnoVrijeme = System.currentTimeMillis();
            //TODO dovršite sami
            long vrijemeZavrsetka = System.currentTimeMillis();

            try {
                sleep(trajanjeSpavanja - (vrijemeZavrsetka - trenutnoVrijeme));
            } catch (InterruptedException ex) {
                Logger.getLogger(RezervnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

            //TODO razmisliti kako izaći iz beskonačne petlje
            //TODO razmisliti kako izaći iz beskonačne petlje
            //TODO razmisliti kako izaći iz beskonačne petlje
            //TODO razmisliti kako izaći iz beskonačne petlje
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Obrađuje korisnika ako ima previše radnih dretvi
     * @param socket
     * @throws IOException
     */
    public void obradiKorisnika(Socket socket) throws IOException {
        this.socket = socket;
        
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = this.socket.getOutputStream();

        StringBuffer stringBuffer = new StringBuffer();

        while (true) {
            int znak = inputStream.read();
            if (znak == -1) {
                break;
            }
            stringBuffer.append((char) znak);
        }

        System.out.println("Primljena naredba: " + stringBuffer);

        try {
            outputStream.write("ERROR 20; Previse klijenata.".getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                this.socket.close();
            } catch (IOException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
