/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.kgrlic.zadaca_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rloka
 */
public class EvidencijaLoader {
    
    public void spremiEvidenciju(Evidencija evidencija, String datoteka) throws FileNotFoundException, IOException{
        FileOutputStream fileOutputStream = new FileOutputStream(datoteka);
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(evidencija);
            objectOutputStream.close();
        }
    }
    
    public Evidencija ucitajEvidencijuSaDiska(String datoteka) {
        File file = new File(datoteka);
        
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (Evidencija) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(EvidencijaLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public Evidencija ucitajEvidencijuSaServera(String datoteka) throws MalformedURLException, IOException {
        URL datotekaNaServeru = new URL(datoteka);
        String tempDatotekaNaDisku = "tmpEvidencija.bin";
        
        try (InputStream inputStream = datotekaNaServeru.openStream()) {
            Files.copy(inputStream, Paths.get(tempDatotekaNaDisku), StandardCopyOption.REPLACE_EXISTING);
        }
        catch(IOException ex){
            Logger.getLogger(EvidencijaLoader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return this.ucitajEvidencijuSaDiska(tempDatotekaNaDisku);
    }
    
    public void ispisiEvidenciju(Evidencija evidencija) {
        
        System.out.println("Broj prekinutih zahtjeva:  " + evidencija.brojPrekinutihZahtjeva);
        System.out.println("Broj uspješnih zahtjeva:  " + evidencija.brojUspjesnihZahtjeva);
        System.out.println("Ukupno zahtjeva:  " + evidencija.ukupnoZahtjeva);
        
    }
}
