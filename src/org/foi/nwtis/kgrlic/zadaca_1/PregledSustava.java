package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 *
 * @author rloka
 */
public class PregledSustava {
    
    private final ArrayList<String> naredba;

    public PregledSustava(ArrayList<String> naredba) {
        this.naredba = naredba;
    }
    
    public void izvrsiPregledNaredbu(){
        String datoteka = this.naredba.get(2);
        System.out.println("Pregled: " + datoteka);
    }
    
}
