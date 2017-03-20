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
    private final ArrayList<String> listaNaredba;
    //TODO varijabla za vrijeme početka rada dretve

    RadnaDretva(Socket socket, ArrayList<RadnaDretva> listaAktivnihRadnihDretvi, short redniBrojDretve) {
        //Postavljanje imena dretve
        //super("kgrlic-" + Short.toUnsignedInt(redniBrojDretve));
        Thread.currentThread().setName("kgrlic-" + Short.toUnsignedInt(redniBrojDretve));
        System.out.println("Pokrenuta dretva: " + Thread.currentThread().getName());
        
        this.socket = socket;
        this.listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi;
        this.listaNaredba = new ArrayList<>();
        
        this.listaNaredba.add("^USER ([^\\s]+); (PASSWD) ([^\\s]+); (PAUSE|STOP|START|STAT);$");
        this.listaNaredba.add("^USER ([^\\s]+); (ADD) ([^\\s]+);$");
        this.listaNaredba.add("^USER ([^\\s]+); (TEST) ([^\\s]+);$");
        this.listaNaredba.add("^USER ([^\\s]+); (WAIT) ([^\\s]+);$");
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        //TODO preuzeti trenutno vrijeme u milisekundama 
        System.out.println(this.getClass());

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
            
            Matcher zaprimljeneNaredbe = this.identificirajNaredbu(stringBuffer);
                        
            if (zaprimljeneNaredbe == null){
                outputStream.write("ERROR 90; Nevaljana naredba.".getBytes());
            }

            switch (zaprimljeneNaredbe.group(2)) {
                case "PASSWD":
                    this.izvrsiAdminNaredbu(zaprimljeneNaredbe, outputStream);
                    break;
                case "ADD":
                    outputStream.write("OK; ADD".getBytes());
                    break;
                case "TEST":
                    outputStream.write("OK; TEST".getBytes());
                    break;
                case "WAIT":
                    outputStream.write("OK; WAIT".getBytes());
                    break;
            }            
            
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

    public Matcher identificirajNaredbu(StringBuffer korisnickaNaredba) {

        for (String naredba : this.listaNaredba) {
            Pattern pattern = Pattern.compile(naredba);
            Matcher matcher = pattern.matcher(korisnickaNaredba);

            if (matcher.matches()) {
                return matcher;
            }
        }

        return null;
    }
    
    public void izvrsiAdminNaredbu(Matcher zaprimljeneNaredbe, OutputStream outputStream) throws IOException{
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
    
    public void izvrsiKlijentAddNaredbu(Matcher matcher, OutputStream outputStream) throws IOException{
        outputStream.write("OK; ADD".getBytes());
    }
    
    public void izvrsiKlijentTestNaredbu(Matcher matcher, OutputStream outputStream) throws IOException{
        outputStream.write("OK; TEST".getBytes());
    }
    
    public void izvrsiKlijentWaitNaredbu(Matcher matcher, OutputStream outputStream) throws IOException{
        outputStream.write("OK; WAIT".getBytes());
    }
}
