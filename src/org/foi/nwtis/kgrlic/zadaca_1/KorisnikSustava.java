/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.ArrayList;

/**
 *
 * @author kgrlic
 */
public class KorisnikSustava {    

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {

        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        ArrayList<String> naredba = KorisnikSustava.provjeriNaredbu(stringBuilder.toString().trim());

        if (naredba != null) {

            KorisnikSustava korisnikSustava = new KorisnikSustava();

            switch (naredba.get(1)) {
                case "admin":
                    AdministratorSustava administratorSustava = new AdministratorSustava(naredba);
                    administratorSustava.izvrsiAdminNaredbu();
                    break;
                case "korisnik":
                    KlijentSustava klijentSustava = new KlijentSustava(naredba);
                    klijentSustava.izvrsiKlijentNaredbu();
                    break;
                case "prikaz":
                    PregledSustava pregledSustava = new PregledSustava(naredba);
                    pregledSustava.izvrsiPregledNaredbu();
                    break;
            }

        } else {
            System.out.println("Proslijeđeni parametri ne odgovaraju! Gasim program ...");
        }
    }

    /**
     * Provjerava valjanost naredbe
     * @param korisnickaNaredba
     * @return 
     */
    private static ArrayList<String> provjeriNaredbu(String korisnickaNaredba) {
        
        Validator validator = new Validator();     
        
        if (validator.stringValjan(korisnickaNaredba, Validator.ADMIN)) {
            return validator.grupe(korisnickaNaredba, Validator.ADMIN);
        } else if (validator.stringValjan(korisnickaNaredba, Validator.KORISNIK)) {
            return validator.grupe(korisnickaNaredba, Validator.KORISNIK);
        } else if (validator.stringValjan(korisnickaNaredba, Validator.PRIKAZ)) {
            return validator.grupe(korisnickaNaredba, Validator.PRIKAZ);
        }
        
        return null;
    }
}
