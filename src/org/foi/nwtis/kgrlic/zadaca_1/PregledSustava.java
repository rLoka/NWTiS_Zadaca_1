package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.ArrayList;

/**
 *
 * @author rloka
 */
public class PregledSustava {

    private final ArrayList<String> naredba;
    private Evidencija evidencija;

    /**
     *
     * @param naredba
     */
    public PregledSustava(ArrayList<String> naredba) {
        this.naredba = naredba;
    }

    /**
     *
     */
    public void izvrsiPregledNaredbu() {
        String datoteka = this.naredba.get(2);

        switch (this.provjeraParametara(datoteka)) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }

        System.out.println("Pregled: " + datoteka);
    }

    private int provjeraParametara(String datoteka) {

        Validator validator = new Validator();

        if (datoteka.toLowerCase().startsWith("http") || datoteka.toLowerCase().startsWith("ftp")) {
            if (validator.datotekaNaServeruPostoji(datoteka)) {
                return 1;
            }
        } else {
            if (validator.datotekaNaDiskuPostoji(datoteka)) {
                return 2;
            }
        }
        return 0;
    }    

}
