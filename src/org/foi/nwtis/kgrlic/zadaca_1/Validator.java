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

    public static final String IPADRESA = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public static final String URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$";
    public static final String POSLUZITELJ = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    public static final String SERVER = "^-konf ([^\\s]+\\.(?i))(txt|xml|bin)( -load)?$";
    public static final String ADMIN = "^-(admin) -server ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -p ([^\\s]+) -((pause)|(start)|(stop)|(stat))$";
    public static final String KORISNIK = "^-(korisnik) -s ([^\\s]+) -port ([\\d]{4}) -u ([^\\s]+) -(a|t|w) ([^\\s]+)|([\\d]{3})$";
    public static final String PRIKAZ = "^-(prikaz) -s ([^\\s]+)$";

    public static final String KORISNICKO_IME = "^[A-z0-9\\_\\-]*$";
    public static final String LOZINKA = "^[A-z0-9\\_\\-\\#\\!]*$";

    public static final String PASSWD = "^USER ([^\\s]+); (PASSWD) ([^\\s]+); (PAUSE|STOP|START|STAT);$";
    public static final String ADD = "^USER ([^\\s]+); (ADD) ([^\\s]+);$";
    public static final String TEST = "^USER ([^\\s]+); (TEST) ([^\\s]+);$";
    public static final String WAIT = "^USER ([^\\s]+); (WAIT) ([^\\s]+);$";

    public static final int[] PORT = {8000, 9999};
    public static final int[] NNN = {1, 600};

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

    public boolean stringValjan(StringBuffer izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);
        return matcher.matches();
    }

    public boolean stringValjan(String izraz, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(izraz);
        return matcher.matches();
    }

    public boolean rasponValjan(int broj, int[] raspon) {
        return broj >= raspon[0] && broj <= raspon[1];
    }

    public boolean rasponValjan(String broj, int[] raspon) {
        int ulazniBroj = Integer.parseInt(broj);
        return ulazniBroj >= raspon[0] && ulazniBroj <= raspon[1];
    }

    public boolean datotekaNaDiskuPostoji(String datoteka) {
        return new File(datoteka).isFile();
    }

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
