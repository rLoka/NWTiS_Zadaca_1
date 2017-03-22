/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
     * Glavna metoda klase ServerSustava - pokreće server.
     *
     * @param args argumenti pri pozivu programa u obliku niza stringova
     */
    public static void main(String[] args) {

        Matcher matcher = ServerSustava.provjeriUlazneParametre(args);

        if (matcher.matches()) {

            int kraj = matcher.groupCount();
            for (int i = 0; i <= kraj; i++) {
                System.out.println(i + ". " + matcher.group(i));
            }

            String nazivDatoteke = matcher.group(1) + matcher.group(2);
            boolean trebaUcitatiEvidenciju = false;
            if (matcher.group(3) != null) {
                trebaUcitatiEvidenciju = true;
            }

            ServerSustava server = new ServerSustava();
            server.pokreniServer(nazivDatoteke, trebaUcitatiEvidenciju);

        } else {
            System.out.println("Prosljeđeni argumenti ne odgovaru predviđenim načinima poziva!");
        }
    }

    /**
     * Metoda provjerava da li su argumenti valjani, tj. vraća Matcher objekt
     * koje koriste ostale metode za sekvencioniranje argumenata.
     *
     *
     * @param args argumenti u obliku stringa
     */
    private static Matcher provjeriUlazneParametre(String[] args) {

        String sintaksa = "^-konf ([^\\s]+\\.(?i))(txt|xml|bin)( +-load)?$";
        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        String ulazniString = stringBuilder.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher matcher = pattern.matcher(ulazniString);
        return matcher;
    }

    private void pokreniServer(String nazivDatoteke, boolean trebaUcitatiEvidenciju) {
        //TODO kreirati kolekciju u kojoj će serijalizatorEvidencije spremati aktivne dretve
        ArrayList<RadnaDretva> listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi = new ArrayList<>();
        ServerRuntimeKonfiguracija serverRuntimeKonfiguracija = ServerRuntimeKonfiguracija.getInstance();

        try {
            Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

            int port = Integer.parseInt(konfiguracija.dajPostavku("port"));
            int maksBrojRadnihDretvi = Integer.parseInt(konfiguracija.dajPostavku("maksBrojRadnihDretvi"));
            short redniBrojDretve = 0;

            NadzorDretvi nadzorDretvi = new NadzorDretvi(konfiguracija, listaAktivnihRadnihDretvi);
            nadzorDretvi.start();

            RezervnaDretva rezervnaDretva = new RezervnaDretva(konfiguracija);
            rezervnaDretva.start();

            ProvjeraAdresa provjeraAdresa = new ProvjeraAdresa(konfiguracija);
            provjeraAdresa.start();

            SerijalizatorEvidencije serijalizatorEvidencije = new SerijalizatorEvidencije(konfiguracija);
            serijalizatorEvidencije.start();

            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();

                if (listaAktivnihRadnihDretvi.size() >= maksBrojRadnihDretvi) {
                    System.out.println("Previše radnih dretvi! Pokrećem rezervnu dretvu.");
                    rezervnaDretva.obradiKorisnika(socket);
                } else {
                    redniBrojDretve++;
                    RadnaDretva radnaDretva = new RadnaDretva(socket, listaAktivnihRadnihDretvi, redniBrojDretve);
                    listaAktivnihRadnihDretvi.add(radnaDretva);
                    radnaDretva.start();
                }
            }

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija | IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Neispravna ili nepostojeća konfiguracijska datoteka! Gasim server ...");
        }
    }

}
