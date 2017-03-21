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
import java.util.HashMap;
import java.util.Map;
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
    private final ArrayList<String> listaNaredbi;
    private final long vrijemePocetka;

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
            InputStream inputStream = null;
            OutputStream outputStream = null;

            Thread.sleep(100);

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

                Matcher zaprimljeneNaredbe = this.identificirajNaredbu(stringBuffer);

                if (zaprimljeneNaredbe == null) {
                    outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
                }

                switch (zaprimljeneNaredbe.group(2)) {
                    case "PASSWD":
                        this.izvrsiAdminNaredbu(zaprimljeneNaredbe, outputStream);
                        break;
                    case "ADD":
                        this.izvrsiKlijentAddNaredbu(zaprimljeneNaredbe, outputStream);
                        break;
                    case "TEST":
                        this.izvrsiKlijentTestNaredbu(zaprimljeneNaredbe, outputStream);
                        break;
                    case "WAIT":
                        this.izvrsiKlijentWaitNaredbu(zaprimljeneNaredbe, outputStream);
                        break;
                }

                outputStream.flush();

            } catch (IOException | NullPointerException ex) {
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
        } catch (InterruptedException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.ukloniDretvuIzListeAktivnihDretvi();
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

    private void izvrsiAdminNaredbu(Matcher zaprimljeneNaredbe, OutputStream outputStream) throws IOException {
        switch (zaprimljeneNaredbe.group(4)) {
            case "PAUSE":
                outputStream.write("OK; PAUSE".getBytes());
                break;
            case "START":
                outputStream.write("OK; START".getBytes());
                break;
            case "STOP":
                outputStream.write("OK; STOP".getBytes());
                break;
            case "STAT":
                outputStream.write("OK; STAT".getBytes());
                break;
            default:
                outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
        }

    }

    private void izvrsiKlijentAddNaredbu(Matcher matcher, OutputStream outputStream) throws IOException {
        outputStream.write("OK; ADD".getBytes());
    }

    private void izvrsiKlijentTestNaredbu(Matcher matcher, OutputStream outputStream) throws IOException {
        outputStream.write("OK; TEST".getBytes());
    }

    private void izvrsiKlijentWaitNaredbu(Matcher matcher, OutputStream outputStream) throws IOException {
        outputStream.write("OK; WAIT".getBytes());
    }

    public long vrijemeIzvodenja() {
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
