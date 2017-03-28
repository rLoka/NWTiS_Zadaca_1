/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rloka
 */
public class Validator {

    /**
     * Regex za provjeru ispravnosti IP adrese
     */
    public static final String IPADRESA = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    /**
     * Regex za provjeru ispravnosti URL-a
     */
    public static final String URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$";

    /**
     * Regex za provjeru ispravnosti adrese poslužitelja
     */
    public static final String POSLUZITELJ = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    /**
     * Regex za provjeru ispravnosti naredbe za pokretanje servera
     */
    public static final String SERVER = "^-konf ([^\\s]+\\.(?i))(txt|xml|bin)( -load)?$";

    /**
     * Regex za provjeru ispravnosti pokretanje klijenta kao admin
     */
    public static final String ADMIN = "^-(admin) -server ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -p ([^\\s]+) -((pause)|(start)|(stop)|(stat))$";

    /**
     * Regex za provjeru ispravnosti pokretanje klijenta kao korisnik
     */
    public static final String KORISNIK = "^-(korisnik) -s ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -(a|t|w) ([^\\s]+)|([\\d]{3})$";

    /**
     * Regex za provjeru ispravnosti pokretanje klijenta za prikaz
     */
    public static final String PRIKAZ = "^-(prikaz) -s ([^\\s]+)$";

    /**
     * Regex za provjeru valjanosti naziva korisničkog imena
     */
    public static final String KORISNICKO_IME = "^[A-z0-9\\_\\-]*$";

    /**
     * Regex za provjeru valjanosti znakova lozinke
     */
    public static final String LOZINKA = "^[A-z0-9\\_\\-\\#\\!]*$";

    /**
     * Regex za provjeru ispravnosti prispjele naredbe administratora
     */
    public static final String PASSWD = "^USER ([^\\s]+); (PASSWD) ([^\\s]+); (PAUSE|STOP|START|STAT);$";

    /**
     * Regex za provjeru ispravnosti prispjele naredbe korisnika za ADD
     */
    public static final String ADD = "^USER ([^\\s]+); (ADD) ([^\\s]+);$";

    /**
     * Regex za provjeru ispravnosti prispjele naredbe korisnika za TEST
     */
    public static final String TEST = "^USER ([^\\s]+); (TEST) ([^\\s]+);$";

    /**
     * Regex za provjeru ispravnosti prispjele naredbe korisnika za WAIT
     */
    public static final String WAIT = "^USER ([^\\s]+); (WAIT) ([^\\s]+);$";

    /**
     * Regex za provjeru ispravnosti porta u zadanom rasponu
     */
    public static final int[] PORT = {8000, 9999};

    /**
     * Regex za provjeru ispravnosti sekundi koje server treba čekati
     */
    public static final int[] NNN = {1, 600};

    /**
     * Vraća grupe iz stringa prema regexu
     * @param izraz
     * @param regex
     * @return
     */
    public ArrayList<String> grupe(StringBuffer izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);

        ArrayList<String> grupe = new ArrayList();

        if (matcher.matches()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                grupe.add(matcher.group(i));
            }
        }

        return grupe;
    }

    /**
     * Vraća grupe iz stringa prema regexu
     * @param izraz
     * @param regex
     * @return
     */
    public ArrayList<String> grupe(String izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);

        ArrayList<String> grupe = new ArrayList();

        if (matcher.matches()) {
            for (int i = 0; i <= matcher.groupCount(); i++) {
                grupe.add(matcher.group(i));
            }
        }

        return grupe;
    }

    /**
     * Vraća informaciju o valjanosti stringa prema regexu
     * @param izraz
     * @param regex
     * @return
     */
    public boolean stringValjan(StringBuffer izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);
        return matcher.matches();
    }

    /**
     * Vraća informaciju o valjanosti stringa prema regexu
     * @param izraz
     * @param regex
     * @return
     */
    public boolean stringValjan(String izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);
        return matcher.matches();
    }

    /**
     * Vraća informaciju o valjanosti porta
     * @param broj
     * @param raspon
     * @return
     */
    public boolean rasponValjan(int broj, int[] raspon) {
        return broj >= raspon[0] && broj <= raspon[1];
    }

    /**
     * Vraća informaciju o valjanosti porta
     * @param broj
     * @param raspon
     * @return
     */
    public boolean rasponValjan(String broj, int[] raspon) {
        int ulazniBroj = Integer.parseInt(broj);
        return ulazniBroj >= raspon[0] && ulazniBroj <= raspon[1];
    }

    /**
     * Vraća informaciju o postojanju datoteke na disku
     * @param datoteka
     * @return
     */
    public boolean datotekaNaDiskuPostoji(String datoteka) {
        return new File(datoteka).isFile();
    }

    /**
     * Vraća informaciju o postojanju datoteke na udaljenom poslužitelju
     * @param datoteka
     * @return
     */
    public boolean datotekaNaServeruPostoji(String datoteka) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con
                    = (HttpURLConnection) new URL(datoteka).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException ex) {
            return false;
        }
    }
}
