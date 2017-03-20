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
    private final ArrayList<RadnaDretva> listaAktivnihRadnihDretvi;
    //TODO varijabla za vrijeme početka rada dretve

    RadnaDretva(Socket socket, ArrayList<RadnaDretva> listaAktivnihRadnihDretvi) {
        this.socket = socket;
        this.listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        //TODO preuzeti trenutno vrijeme u milisekundama 
        System.out.println(this.getClass());

        String sintaksa_admin = "^USER ([^\\s]+); PASSWD ([^\\s]+); (PAUSE|STOP|START|STAT);$";
        String sintaksa_korisnik_1 = "USER ([^\\s]+); ADD ([^\\s]+);";
        String sintaksa_korisnik_2 = "USER ([^\\s]+); TEST ([^\\s]+);";
        String sintaksa_korisnik_3 = "USER ([^\\s]+); WAIT ([^\\s]+);";

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            StringBuffer stringBuffer = new StringBuffer();
            
            while (true) {
                int znak = inputStream.read();
                if (znak == -1) {
                    break;
                }
                stringBuffer.append((char) znak);
            }
            
            System.out.println("Primljena naredba: " + stringBuffer);

            //TODO provjeri ispravnost pripremljenog zahtjeva
            Pattern pattern = Pattern.compile(sintaksa_admin);
            Matcher matcher = pattern.matcher(stringBuffer);
            boolean status = matcher.matches();
            if (status) {
                //TODO dobršiti za admina
            } else {
                pattern = Pattern.compile(sintaksa_korisnik_1);
                matcher = pattern.matcher(stringBuffer);
                status = matcher.matches();
                if (status) {
                    //TODO dovršiti za korisnika 1. slučaj
                } else {
                    pattern = Pattern.compile(sintaksa_korisnik_2);
                    matcher = pattern.matcher(stringBuffer);
                    status = matcher.matches();
                    if (status) {
                        //TODO dovršiti za korisnika 2. slučaj
                    } else {
                        //TODO i tako za sve ostale slučajve
                    }
                }
            }

            outputStream.write("OK;".getBytes());
            outputStream.flush();
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

        //TODO obrisati dretvu iz kolekcije aktivnih radnih dretvi
        //TODO smanjiti brojač aktivnih radnih dretvi
        //TODO ažurirati evidenciju rada
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

}
