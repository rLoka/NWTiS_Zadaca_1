/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kgrlic
 */
public class KorisnikSustava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<String> listaNaredbi = new ArrayList<>();

        listaNaredbi.add("^-(admin) -server ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -p ([^\\s]+) -((pause)|(start)|(stop)|(stat))$");
        listaNaredbi.add("^-(user) -s ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -(a|t|w) ([^\\s]+)|([\\d]{3})$");
        listaNaredbi.add("^-(prikaz) -s ([^\\s]+)$");

        StringBuilder stringBuilder = new StringBuilder();
        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        Matcher matcher = KorisnikSustava.identificirajNaredbu(stringBuilder.toString().trim(), listaNaredbi);

        if (matcher.matches()) {

            KorisnikSustava korisnikSustava = new KorisnikSustava();

            switch (matcher.group(1)) {
                case "admin":
                    AdministratorSustava administratorSustava = new AdministratorSustava(matcher);
                    administratorSustava.izvrsiAdminNaredbu();
                    break;
                case "user":
                    KlijentSustava klijentSustava = new KlijentSustava(matcher);
                    klijentSustava.izvrsiKlijentNaredbu();
                    break;
                case "prikaz":
                    PregledSustava pregledSustava = new PregledSustava(matcher);
                    pregledSustava.izvrsiPregledNaredbu();                    
                    break;
            }

        } else {
            System.out.println("Proslijeđeni parametri ne odgovaraju! Gasim program ...");
        }
    }

    private static Matcher identificirajNaredbu(String korisnickaNaredba, ArrayList<String> listaNaredbi) {

        for (String naredba : listaNaredbi) {
            Pattern pattern = Pattern.compile(naredba);
            Matcher matcher = pattern.matcher(korisnickaNaredba);
            if (matcher.matches()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    System.out.println(i + ". " + matcher.group(i));
                }
                return matcher;
            }
        }

        return null;
    }
}