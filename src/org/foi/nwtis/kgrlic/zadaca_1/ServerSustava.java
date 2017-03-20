/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
public class ServerSustava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //-konf datoteka(.txt | .xml) [-load]
        String sintaksa = "^-konf ([^\\s]+\\.(?i))(txt|xml|bin)( +-load)?$";

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
            int poc = 0;
            int kraj = m.groupCount();
            for (int i = poc; i <= kraj; i++) {
                System.out.println(i + ". " + m.group(i));
            }
            
            String nazivDatoteke = m.group(1) + m.group(2);
            boolean trebaUcitatiEvidenciju = false;
            if(m.group(3) != null){
                trebaUcitatiEvidenciju = true;
            }
            
            ServerSustava server = new ServerSustava();
            server.pokreniServer(nazivDatoteke,trebaUcitatiEvidenciju);
            
        } else {
            System.out.println("Ne odgovara!");
        }
    }

    private void pokreniServer(String nazivDatoteke, boolean trebaUcitatiEvidenciju) {
        //TODO kreirati kolekciju u kojoj će se spremati aktivne dretve
        try {
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
            
            int port = Integer.parseInt(konfig.dajPostavku("port"));
            
            NadzorDretvi nd = new NadzorDretvi(konfig);
            nd.start();
            RezervnaDretva rezervnaDretva = new RezervnaDretva(konfig);
            rezervnaDretva.start();
            ProvjeraAdresa pa = new ProvjeraAdresa(konfig);
            pa.start();
            SerijalizatorEvidencije se = new SerijalizatorEvidencije(konfig);
            se.start();
            
            ServerSocket serverSocket = new ServerSocket(port);
            
            while (true) {                
                Socket socket = serverSocket.accept();
                RadnaDretva rd = new RadnaDretva(socket);
                //TODO dodaj dretvu u kolekciju aktivnih radnih dretvi
                rd.start();
                
                //TODO treba provjeriti ima li "mjesta" za novu radnu dretvu
            }
            
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija | IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}
