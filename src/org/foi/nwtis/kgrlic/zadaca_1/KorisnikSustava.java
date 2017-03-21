/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.kgrlic.konfiguracije.Konfiguracija;
import org.foi.nwtis.kgrlic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.kgrlic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.kgrlic.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author kgrlic
 */
public class KorisnikSustava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<String> listaNaredbi = new ArrayList<>();

        listaNaredbi.add("^-(admin) -server ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -p ([^\\s]+) -((pause)|(start)|(stop)|(stat))$");
        listaNaredbi.add("^-(user) -s ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) (-a|-t|-w) ([^\\s]+)|([\\d]{3})$");
        listaNaredbi.add("^-(prikaz) -s ([^\\s]+)$");

        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        Matcher matcher = KorisnikSustava.identificirajNaredbu(stringBuilder.toString().trim(), listaNaredbi);

        if (matcher.matches()) {

            KorisnikSustava korisnikSustava = new KorisnikSustava();

            switch (matcher.group(1)) {
                case "admin":
                    korisnikSustava.izvrsiAdminNaredbu(matcher);
                    break;
                case "user":
                    System.out.println("User!");
                    break;
                case "prikaz":
                    System.out.println("Prikaz!");
                    break;
            }

        } else {
            System.out.println("Proslijeđeni parametri ne odgovaraju! Gasim program ...");
        }
    }

    private static Matcher identificirajNaredbu(String korisnickaNaredba, ArrayList<String> listaNaredbi) {

        for (String naredba : listaNaredbi) {
            Pattern pattern = Pattern.compile(naredba);
            Matcher matcher = pattern.matcher(korisnickaNaredba);
            if (matcher.matches()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    System.out.println(i + ". " + matcher.group(i));
                }
                return matcher;
            }
        }

        return null;
    }

    private void izvrsiAdminNaredbu(Matcher matcher) {
        String server = matcher.group(2);
        int port = Integer.parseInt(matcher.group(3));
        String korisnik = matcher.group(4);
        String lozinka = matcher.group(5);
        String naredba = matcher.group(6);

        InputStream inputStream = null;
        OutputStream outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            String zahtjev = "USER " + korisnik + "; PASSWD " + lozinka + "; " + naredba.toUpperCase() + ";";
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

    private void izvrsiKorisnikNaredbu() {

    }

    private void izvrsiPrikazNaredbu() {

    }
}
