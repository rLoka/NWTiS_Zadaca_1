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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kgrlic
 */
public class RadnaDretva extends Thread {

    private Socket s;
    //TODO varijabla za vrijeme početka rada dretve

    RadnaDretva(Socket socket) {
        this.s = socket;
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

        InputStream is = null;
        OutputStream os = null;

        try {
            is = s.getInputStream();
            os = s.getOutputStream();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println("Primljena naredba: " + sb);

            //TODO provjeri ispravnost pripremljenog zahtjeva
            Pattern p = Pattern.compile(sintaksa_admin);
            Matcher m = p.matcher(sb);
            boolean status = m.matches();
            if (status) {
                //TODO dobršiti za admina
            } else {
                p = Pattern.compile(sintaksa_korisnik_1);
                m = p.matcher(sb);
                status = m.matches();
                if (status) {
                    //TODO dovršiti za korisnika 1. slučaj
                } else {
                    p = Pattern.compile(sintaksa_korisnik_2);
                    m = p.matcher(sb);
                    status = m.matches();
                    if (status) {
                        //TODO dovršiti za korisnika 2. slučaj
                    } else {
                        //TODO i tako za sve ostale slučajve
                    }
                }
            }

            os.write("OK;".getBytes());
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
                s.close();
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
