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
     * @throws org.foi.nwtis.kgrlic.konfiguracije.NemaKonfiguracije
     * @throws org.foi.nwtis.kgrlic.konfiguracije.NeispravnaKonfiguracija
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws NemaKonfiguracije, NeispravnaKonfiguracija, IOException {

        StringBuilder stringBuilder = new StringBuilder();
        Validator validator = new Validator();

        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        String ulazniString = stringBuilder.toString().trim();

        if (validator.stringValjan(ulazniString, Validator.SERVER)) {

            ArrayList<String> argumenti = validator.grupe(ulazniString, Validator.SERVER);

            String nazivDatoteke = argumenti.get(1) + argumenti.get(2);
            boolean trebaUcitatiEvidenciju = false;
            if (argumenti.size() > 3) {
                if ("-load".equals(argumenti.get(3).replaceAll("\\s+", ""))) {
                    trebaUcitatiEvidenciju = true;
                }
            }

            ServerSustava server = new ServerSustava();
            server.pokreniServer(nazivDatoteke, trebaUcitatiEvidenciju);

        } else {
            System.out.println("Prosljeđeni argumenti ne odgovaru predviđenim načinima poziva!");
        }
    }

    /**
     * Pokreće server
     * @param nazivDatoteke
     * @param trebaUcitatiEvidenciju
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija
     * @throws IOException 
     */
    private void pokreniServer(String nazivDatoteke, boolean trebaUcitatiEvidenciju) throws NemaKonfiguracije, NeispravnaKonfiguracija, IOException {
       
        ArrayList<RadnaDretva> listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi = new ArrayList<>();
        ServerRuntimeKonfiguracija serverRuntimeKonfiguracija = ServerRuntimeKonfiguracija.getInstance();
        Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);

        Validator validator = new Validator();

        if (trebaUcitatiEvidenciju) {
            String evidencijskaDatoteka = konfiguracija.dajPostavku("evidDatoteka");

            if (evidencijskaDatoteka.length() == 0 || evidencijskaDatoteka == null) {
                System.out.println("Evidencijska datoteka nije definirana.");
            } else {
                boolean datotekaNaDiskuPostoji = validator.datotekaNaDiskuPostoji(evidencijskaDatoteka);
                if (datotekaNaDiskuPostoji) {
                    EvidencijaLoader evidencijaLoader = new EvidencijaLoader();
                    evidencijaLoader.spremiEvidenciju(Evidencija.getInstance(), evidencijskaDatoteka);
                    Evidencija.setInstance(evidencijaLoader.ucitajEvidencijuSaDiska(evidencijskaDatoteka));
                }
                else{
                    System.out.println("Evidencijska datoteka ne postoji.");
                }
            }
        }

        try {

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
                    RadnaDretva radnaDretva = new RadnaDretva(socket, listaAktivnihRadnihDretvi, redniBrojDretve, konfiguracija);
                    listaAktivnihRadnihDretvi.add(radnaDretva);
                    radnaDretva.start();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerSustava.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Neispravna ili nepostojeća konfiguracijska datoteka! Gasim server ...");
        }
    }

}
