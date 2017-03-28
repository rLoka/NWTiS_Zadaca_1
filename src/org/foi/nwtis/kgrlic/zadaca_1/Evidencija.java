/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Evidencija rada sadrži: - podatke o ukupnom broju zahtjeva - broju uspješnih
 * zahtjeva - broju prekinutih zahtjeva - broju zahtjeva s pojedine adrese s
 * koje je poslan zahtjev - status pojedine adrese (URL) koju je
 * korisnik(klijent) dodao - zadnji broj radne dretve - ukupno vrijeme rada
 * radnih dretvi
 *
 * @author kgrlic
 */
public class Evidencija implements Serializable {

    private transient static Evidencija evidencija;

    /**
     * Podatak o ukupnom broju zahtjeva
     */
    public int ukupnoZahtjeva = 0;

    /**
     * Podatak o broju uspješnih zahtjeva
     */
    public int brojUspjesnihZahtjeva = 0;

    /**
     * Podatak o broju prekinutih zahtjeva
     */
    public int brojPrekinutihZahtjeva = 0;

    /**
     * Podatak o zadnjem broju radne dretve
     */
    public short zadnjiBrojRadneDretve = 0;

    /**
     * Podatak o ukupnom vremenu radnih dretvi
     */
    public long ukupnoVrijemeRadaRadnihDretvi = 0;

    /**
     * Podatak o broju zahtijeva sa neke adrese
     */
    public HashMap<String, Integer> brojZahtjevaSaAdrese = new HashMap<>();

    /**
     * Podatak o statusu adrese
     */
    public HashMap<String, String> statusAdrese = new HashMap<>();

    /**
     * Podatak o zahtijevima za dodavanje adrese
     */
    public ArrayList<String> zahtijeviAdrese = new ArrayList<>();

    /**
     *
     */
    public Evidencija() {
    }

    /**
     * Vraća instancu klase (Singleton)
     * @return
     */
    public static Evidencija getInstance() {
        if (Evidencija.evidencija == null) {
            Evidencija.evidencija = new Evidencija();
        }

        return Evidencija.evidencija;
    }

    /**
     * Postavlja instancu klase (Singleton)
     * @param evidencija
     */
    public static void setInstance(Evidencija evidencija) {
        Evidencija.evidencija = evidencija;
    }
}
