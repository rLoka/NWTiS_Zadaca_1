/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.kgrlic.konfiguracije.Konfiguracija;

/**
 *
 * @author kgrlic
 */
public class NadzorDretvi extends Thread {

    private final Konfiguracija konfiguracija;
    private final ArrayList<RadnaDretva> listaAktivnihRadnihDretvi;
    private final long maksVrijemeRadneDretve;
    private final int intervalNadzorneDretve;

    public NadzorDretvi(Konfiguracija konfiguracija, ArrayList<RadnaDretva> listaAktivnihRadnihDretvi) {
        this.konfiguracija = konfiguracija;
        this.listaAktivnihRadnihDretvi = listaAktivnihRadnihDretvi;
        this.maksVrijemeRadneDretve = Long.parseLong(konfiguracija.dajPostavku("maksVrijemeRadneDretve"));
        this.intervalNadzorneDretve = Integer.parseInt(konfiguracija.dajPostavku("intervalNadzorneDretve"));
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {

        while (true) {
            System.out.println(this.getClass());
            long trenutnoVrijeme = System.currentTimeMillis();

            this.provjeriRadneDretve();

            //TODO dovršite sami
            //TODO provjerite trajanje pojedine aktivne radne dretve iz kolekcije
            //TODO obrisati dretvu iz kolekcije aktivnih radnih dretvi ako traje više nego što smije
            long vrijemeZavrsetka = System.currentTimeMillis();

            try {
                Thread.sleep(Math.abs(this.intervalNadzorneDretve - (vrijemeZavrsetka - trenutnoVrijeme)));
            } catch (InterruptedException ex) {
                Logger.getLogger(NadzorDretvi.class.getName()).log(Level.SEVERE, null, ex);
            }

            //TODO razmisliti kako izaći iz beskonačne petlje
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    private void provjeriRadneDretve() {
        //Temp lista se koristi zbog java.util.ConcurrentModificationException
        ArrayList<RadnaDretva> listaDretviKojeTrebaPrekinuti = new ArrayList();

        this.listaAktivnihRadnihDretvi.forEach((radnaDretva) -> {
            String imeRadneDretve = radnaDretva.getName();
            Long vrijemeIzvodenja = radnaDretva.vrijemeIzvodenja();
            System.out.println("Dretva " + imeRadneDretve + " se izvodi već " + vrijemeIzvodenja + "ms.");
            if (vrijemeIzvodenja > this.maksVrijemeRadneDretve) {
                listaDretviKojeTrebaPrekinuti.add(radnaDretva);
            }
        });

        listaDretviKojeTrebaPrekinuti.forEach((radnaDretva) -> {
            radnaDretva.interrupt();
        });
        
    }
}
