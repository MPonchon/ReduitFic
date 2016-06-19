/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reduitfic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author Marc
 * java -jar "reduitFic.jar" -ifileini.ini -ofileout.csv -sfilesrc.csv
 * utilisation :
 *  
 *  
 *
 */
public class ReduitFic {
    // actions possibles
    static public int ACTION_VERSION = 0;
    static public int ACTION_HELP = 1;
    static public int ACTION_REDUIT = 2;
    // membres
    String[] params; // listes des parametres passés en arguments
    //char action; // 'e' : exe, 'h': help
    String version = "ReduitFic v1.1";
    String DescritonVersion = "Modification parametres.";
    
    File fileIni, fileOut, fileSrc;
    int action; // action choisie
    
    // contient les noms des champs
    HashSet<String> fieldsIni = new HashSet<String>();
    HashMap<String,HashSet<String>> mapFiltres = new HashMap<String,HashSet<String>> ();
    
    /**
     * création de l'instance.
     * Initialise l'action a mener
     * @param args 
     */
    public ReduitFic(String[] args) {
        try {
            action = chooseAction(args);
        }
        catch (FileNotFoundException e){
            if (action == ACTION_REDUIT ){
                System.err.println(e.getMessage());
                action = ACTION_HELP;
            }
            else {
                System.out.println("Attention, (" + e.getMessage() +") .");
            }
        }
        catch(Exception e){
            if (action == ACTION_REDUIT ){
                System.err.println("Exception : " + e.getMessage());
                action = ACTION_HELP;
                //e.printStackTrace();
            }
            else {
                e.printStackTrace();
            }
        }

    }    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ReduitFic FR = new ReduitFic(args);
        FR.executeAction();

    }

    /**
     * Choix de l'action en fonction des arguments.
     * @param args
     * @return l'action choisie
     */
    public int chooseAction(String[] args) throws FileNotFoundException {
        int action = ACTION_VERSION;
        
        //System.out.println("args : ["+ args.length+"]");
        for (String param : args) {
            //System.out.println("param : ["+ param + "]");
            if (param.length() < 2) continue;
            if (param.charAt(0) != '-') continue;
 
            char opt = param.charAt(1); // 0 : - 
           // System.out.println("param : ["+ param + "], opt :[" +opt +"]");
            // options prioritaires, sortie de boucle
            if (opt == 'v') { return ACTION_VERSION; } 
            if (opt == 'h') { return ACTION_HELP; }
            
            String fileName = param.substring(2, param.length());
            if (opt == 'i') { 
                action = this.action =  ACTION_REDUIT; 
                //System.out.println("fileName [" + fileName + "]");
                fileIni = new File(fileName);
                if (!fileIni.isFile()) { throw new FileNotFoundException("fileIn -i incorrect : " + fileName); }
                
            }
            if (opt == 's') { 
                //System.out.println("fileName [" + fileName + "]");
                action = this.action  =  ACTION_REDUIT; 
                fileSrc = new File(fileName);
                if (!fileSrc.isFile()) { throw new FileNotFoundException("fileSource -s incorrect : " + fileName); }
            }  
            if (opt == 'o') { 
                System.out.println("fileName [" + fileName + "]");
                fileOut = new File(fileName);
                action  =  ACTION_REDUIT; 
            }    
        }    
        return action;
    }
    
    /**
     * Execution de l'action
     */
    public void executeAction() {
        if (action == ACTION_VERSION) {
            afficheVersion();
        }
        else if (action == ACTION_HELP) {
            AfficheHelp();
        }
        else if (action == ACTION_REDUIT) {
            reduitColonnes();
        }       
    }
    
    /**
     * rempli le HashSet avec fileini
     */
    public void fillIni() {
       // StringBuilder contents = new StringBuilder();
        try {
          //use buffering, reading one line at a time
          //FileReader always assumes default encoding is OK!
          BufferedReader input =  new BufferedReader(new FileReader(fileIni));
            try {
                String line = null; //not declared within while loop
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while (( line = input.readLine()) != null){
                  //contents.append(line);

                  // rempli les filtres pour chaque colonne
                  String[] parts = line.split(";");
//                  System.out.println("fillIni :: line :[" + line + "] parts :" +  parts.length );
                  if (parts.length > 1) {
                      HashSet<String> values = null;
                      fieldsIni.add(parts[0]);
                      for (int i = 1; i < parts.length; i++) {
                          values = mapFiltres.get(parts[0]);
                          if (values == null) {
                              values = new HashSet<String>();
                          }
                          values.add(parts[i]);
//                          System.out.println("ajout  ::" +parts[0]+ ", " +parts[i]+ ".");
                          mapFiltres.put(parts[0], values);
                      }
                  }
                  else {
                    //contents.append(System.getProperty("line.separator"));
                    fieldsIni.add(line);
                  }
                }
            }
            finally {
              input.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * lire le fileSrc et retourne un array list de string avec les colonnes utiles
     */
    public ArrayList<String> readInList() {
        ArrayList<String> data = new ArrayList<String>();
//        System.out.println("fieldsIni contient " + fieldsIni.size()+ " elem");
        try {
         //use buffering, reading one line at a time
         //FileReader always assumes default encoding is OK!
            BufferedReader input =  new BufferedReader(new FileReader(fileSrc));    
            String line = null; //
            StringBuilder buildLine = new StringBuilder();    
            // lecture des titres
            String titresSrc = null;
            String[] keys = null;
            if ((titresSrc = input.readLine()) != null) {
                keys = titresSrc.split(";");
            }
            if (keys == null) {
                throw new IllegalArgumentException("le fichier source ne contient pas d'entete");
            }
            // construction de l'ensemble des colonnes utiles
            HashSet<Integer> colonnes = new HashSet<Integer>();
            for (int i = 0; i < keys.length - 1; i++) {
                if (fieldsIni.contains(keys[i])) {
                    colonnes.add(i);
                    buildLine.append(keys[i] + ";"); // ecriture des titres
                    //System.out.println("colonne add : i :" + i + "]");
                }
            }
            if (fieldsIni.contains(keys[keys.length - 1])) {
                colonnes.add(keys.length - 1);
                buildLine.append(keys[keys.length - 1]); // ecriture du dernier champ des titres
            }                
            data.add(buildLine.toString());

//            int noLine = 0;        
            // lectures des données        
            while (( line = input.readLine()) != null){
               // System.out.println("noLine :" + noLine + ", line [" + line + "]");
                StringBuilder sbLineOut = new StringBuilder();
                String[] dataLine = line.split(";");
                
                boolean filtreOk = true;
                for (int i = 0; i < dataLine.length; i++) {
                    //System.out.println("noLine :" + noLine + ", dataLine ["+ i + "] dataLine [" + dataLine[i] + "]");
                    if (colonnes.contains(i)) {
                        HashSet<String> setFiltreKeys = mapFiltres.get(keys[i]);
//                        System.out.println("keys[" + i + "] (" + keys[i] +")");
                        if (setFiltreKeys != null && !setFiltreKeys.contains(dataLine[i])) {
                            //System.out.println("valeur nok -> VIREE : dataLine[" + i + "] " +dataLine[i]);
                            filtreOk = false;
                        }
//                        //debug
//                        else if (setFiltreKeys != null){
//
//                            System.out.println("ok dataLine[" + i + "] (" + dataLine[i] +")");
//                        }
                        sbLineOut.append(dataLine[i]);
                        sbLineOut.append(";") ;
                       // System.out.println("noLine :" + noLine + ", sbLineOut [" + sbLineOut.toString() + "]");
                    }
                }
                if (filtreOk && sbLineOut.length() > 0) {
                    data.add(sbLineOut.substring(0, sbLineOut.length() - 1)); // enleve le dernier ;
                   // System.out.println("noLine :" + (noLine +2) + ", sbLineOut [" + sbLineOut.substring(0, sbLineOut.length() - 1) + "]");
                }
//                noLine++;
            }
            input.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        return data;
    }
    
    /**
     * Ecrit le arraylist dans un fichier
     */
    public void writeToFile(ArrayList<String> data) {
        //use buffering, reading one line at a time
        //FileReader always assumes default encoding is OK!
        //int noLine = 0; 
       // System.out.println("writeToFile : fileOut ["+fileOut+"]");
        try {
            FileWriter fileWriter =  new FileWriter(fileOut);  // Assume default encoding.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // Always wrap FileWriter in BufferedWriter.
            try {
                for (String line : data) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    //System.out.println(noLine +", "+line);
                    //noLine++;
                }
                
            }
            catch (IOException ex){
                ex.printStackTrace();
            }            
            finally {
                bufferedWriter.close();
                fileWriter.close();
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }           
    }
    
    
    /**
     * lire le fileSrc et ecrit dans le fileOut si le champs est present dans le hashset 
     * Bug : ne va pas ecrire jusqu'au bout du fichier
     */
    private void readWrite() {
        try {
         //use buffering, reading one line at a time
         //FileReader always assumes default encoding is OK!
            BufferedReader input =  new BufferedReader(new FileReader(fileSrc));
            FileWriter fileWriter =  new FileWriter(fileOut);  // Assume default encoding.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // Always wrap FileWriter in BufferedWriter.
            try {
                String line = null; //
                
                // lecture des titres
                String titresSrc = null;
                String[] keys = null;
                if ((titresSrc = input.readLine()) != null) {
                    keys = titresSrc.split(";");
                }
                if (keys == null) {
                    throw new IllegalArgumentException("le fichier source ne contient pas d'entete");
                }
                ArrayList<Integer> colonnes = new ArrayList<Integer>();
                for (int i = 0; i < keys.length - 1; i++) {
                    if (fieldsIni.contains(keys[i])) {
                        colonnes.add(i);
                        bufferedWriter.write(keys[i] + ";"); // ecriture des titres
                    }
                }
                if (fieldsIni.contains(keys[keys.length - 1])) {
                        colonnes.add(keys.length - 1);
                        bufferedWriter.write(keys[keys.length - 1]); // ecriture du dernier champ des titres
                }                
                bufferedWriter.newLine();
                int noLine = 0;        
                // lectures des données        
                while (( line = input.readLine()) != null){
                    StringBuilder sbLineOut = new StringBuilder();
                    String[] data = line.split(";");;
                    for (int i = 0; i < data.length; i++) {
                        if (colonnes.contains(i)) {
                            
                            sbLineOut.append(data[i]);
                            sbLineOut.append(";") ;
                            //System.out.println("noLine :" + noLine + ", champ :[" + keys[i] + "] : OK , value : [" + data[i] +"] sbLineOut [" + sbLineOut.toString() + "]");
                        }
                    }
                    if (sbLineOut.length() > 0) {
                        bufferedWriter.write(sbLineOut.substring(0, sbLineOut.length() - 1)); // enleve le dernier ;
                        bufferedWriter.newLine();
                        System.out.println("noLine :" + (noLine +2) + ", sbLineOut [" + sbLineOut.substring(0, sbLineOut.length() - 1) + "]");
                    }
                    noLine++;
                }
            }
            finally {
                fileWriter.flush();
                input.close();
                fileWriter.close();
            }
            fileWriter.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }   
        
    }
    

    
    public void reduitColonnes() {
        fillIni(); // rempli le HashSet avec fileini
        writeToFile(readInList());
        if (fileOut.isFile()) {
            System.out.println("Fichier [" + fileOut + "] cree.");
        } else {
            System.out.println("Erreur de creation du fichier [" + fileOut + "].");
        }
    }
    
    // --------------------
    
    /*
     * aide en ligne de commande   
     */
    public void AfficheHelp() {
        afficheVersion();
        System.out.println("Aide : reduit le nombre de colonne d'un fichier.");
        StringBuilder str = new StringBuilder();
        str.append("options :\n");
        str.append("-ifichierInit.ini : indiquez les noms des champs (colonnes) à conserver\n");
        str.append("optionnel liste valeurs pour le champ ;val1;val2 ...\n");
        str.append("-ofichierOut : nom du fichier de sortie\n");
        str.append("-sfichierSource : nom du fichier du fichier source\n");
        str.append("\n");
        str.append("Utilisation: java -jar \"reduitFic.jar\" -ifileini.ini -ofileout.csv -sfilesrc.csv \n");
        
        System.out.println(str.toString());
    }
    
    private void afficheVersion() {
        System.out.println(version);
    }
}
