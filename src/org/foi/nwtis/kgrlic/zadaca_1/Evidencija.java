/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.Serializable;
import java.util.HashMap;

/**
 *     Evidencija rada sadrži:
     - podatke o ukupnom broju zahtjeva
     - broju uspješnih zahtjeva
     - broju prekinutih zahtjeva
     - broju zahtjeva s pojedine adrese s koje je poslan zahtjev
     - status pojedine adrese (URL) koju je korisnik(klijent) dodao
     - zadnji broj radne dretve
     - ukupno vrijeme rada radnih dretvi    
 * @author kgrlic
 */
public class Evidencija implements Serializable {

    private transient static Evidencija evidencija;

    public int ukupnoZahtjeva = 0;
    public int brojUspjesnihZahtjeva = 0;
    public int brojPrekinutihZahtjeva = 0;
    public short zadnjiBrojRadneDretve = 0;
    public long ukupnoVrijemeRadaRadnihDretvi = 0;
    public HashMap<String, Integer> brojZahtjevaSaAdrese = new HashMap<>();
    public HashMap<String, String> statusAdrese = new HashMap<>();

    public Evidencija() {
    }

    public static Evidencija getInstance() {
        if(Evidencija.evidencija == null){
            Evidencija.evidencija = new Evidencija();
        }
        
        return Evidencija.evidencija;
    }

    public static void setInstance(Evidencija evidencija) {
        Evidencija.evidencija = evidencija;
    }
}
