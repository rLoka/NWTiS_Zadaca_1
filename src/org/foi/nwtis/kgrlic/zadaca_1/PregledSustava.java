package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.regex.Matcher;

/**
 *
 * @author rloka
 */
public class PregledSustava {
    
    private final Matcher matcher;

    public PregledSustava(Matcher matcher) {
        this.matcher = matcher;
    }
    
    public void izvrsiPregledNaredbu(){
        String datoteka = this.matcher.group(2);
        System.out.println("Pregled: " + datoteka);
    }
    
}
