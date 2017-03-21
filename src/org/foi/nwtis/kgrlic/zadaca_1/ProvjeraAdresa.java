/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.kgrlic.konfiguracije.Konfiguracija;

/**
 *
 * @author kgrlic
 */
public class ProvjeraAdresa extends Thread{

    Konfiguracija konfiguracija;
    
    public ProvjeraAdresa(Konfiguracija konfiguracija) {
        this.konfiguracija = konfiguracija;
    }
    
    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        int trajanjeSpavanja = Integer.parseInt(konfiguracija.dajPostavku("intervalAdresneDretve"));
        
        while (true) {        
            System.out.println(this.getClass());
            long trenutnoVrijeme = System.currentTimeMillis();
            //TODO dovršite sami
            long vrijemeZavrsetka = System.currentTimeMillis();
            
            try {
                sleep(trajanjeSpavanja- (vrijemeZavrsetka - trenutnoVrijeme));
            } catch (InterruptedException ex) {
                Logger.getLogger(ProvjeraAdresa.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //TODO razmisliti kako izaći iz beskonačne petlje
            
            //TODO razmisliti kako izaći iz beskonačne petlje
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
