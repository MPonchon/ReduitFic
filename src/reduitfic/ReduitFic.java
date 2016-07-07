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
import java.util.regex.Pattern;

/**
 * Reduit les colonnes d'un fichier csv
 *
 * @author Marc
 *
 * utilisation : java -jar "reduitFic.jar" -ifileini.ini -ofileout.csv
 * -sfilesrc.csv
 */
public class ReduitFic {

    // actions possibles
    static public int ACTION_VERSION = 0;
    static public int ACTION_HELP = 1;
    static public int ACTION_REDUIT = 2;

    String version = "ReduitFic v1.3";
    String DescritonVersion = "Modification parametres.";

    String[] fileName;  // nom des fichiers
    static private int FILE_INI = 0;
    static private int FILE_SRC = 1;
    static private int FILE_OUT = 2;

    int action; // action choisie

    // contient les noms des champs
    HashSet<String> fieldsIni = new HashSet<String>();
    // filtre associées aux colonnes
    HashMap<String, String> mapFiltres = new HashMap<String, String>();

    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    /**
     * création de l'instance. Initialise l'action a mener
     *
     * @param args
     */
    public ReduitFic(String[] args) {
        fileName = new String[3];
        action = chooseAction(args);
        /*
        for (String file : fileName) {
            System.out.println("file :[" + file + "] ");
        }
        */
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ReduitFic FR = new ReduitFic(args);
        FR.executeAction();
    }

    /**
     * Choix de l'action en fonction des arguments.
     *
     * @param args
     * @return l'action choisie
     */
    public int chooseAction(String[] args) {
        int action = ACTION_VERSION;

        //System.out.println("args : ["+ args.length+"]");
        for (String param : args) {
            //System.out.println("param : ["+ param + "]");
            if (param.length() < 2) {
                continue;
            }
            if (param.charAt(0) != '-') {
                continue;
            }

            char opt = param.charAt(1); // 0 : - 
            // System.out.println("param : ["+ param + "], opt :[" +opt +"]");
            // options prioritaires, sortie de boucle
            // par ordre de priorité de traitement
            if (opt == 'v') {
                return ACTION_VERSION;
            }
            if (opt == 'h') {
                return ACTION_HELP;
            }

            String file = ltrim(param.substring(2, param.length())); // supprime les espaces entre option et le reste
            if (opt == 'i') {
                action = this.action = ACTION_REDUIT;
                fileName[FILE_INI] = file;
            }
            if (opt == 's') {
                //System.out.println("fileName [" + fileName + "]");
                action = this.action = ACTION_REDUIT;
                fileName[FILE_SRC] = file;
            }
            if (opt == 'o') {
                action = ACTION_REDUIT;
                fileName[FILE_OUT] = file;
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
        } else if (action == ACTION_HELP) {
            AfficheHelp();
        } else if (action == ACTION_REDUIT) {
            reduitColonnes();
        }
    }
    
    /**
     * cree le fichier et ajoute le rep courant si necessaire
     * @param name nom du fichier a creer
     * @return le fichier 
     */
    protected File createFile(String name) {
        //System.err.println("createFile [" + name + "]");
        // si le nom ne contient des "\" ajoute le rep courant
        if (!name.contains("\\")) {
            name = System.getProperty("user.dir") +"\\" + name;
        }
        //System.err.println("path [" + name + "]");
        return new File(name);
    }

    /**
     * rempli le HashSet avec fileini
     */
    public void fillIni() {
        // StringBuilder contents = new StringBuilder();

        try {
            // creation du fichier
            //File fileIni = new File(fileName[FILE_INI]);
            File fileIni = createFile(fileName[FILE_INI]);
            if (!fileIni.isFile()) {
                throw new FileNotFoundException("fileIn -i incorrect : " + fileName[FILE_INI]);
            }

            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(fileIni));
            try {
                String line = null; //not declared within while loop
                /*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                  //contents.append(line);

                    // rempli les filtres pour chaque colonne
                    String[] parts = line.split(";");
//                  System.out.println("fillIni :: line :[" + line + "] parts :" +  parts.length );
                    if (parts.length > 1) {
                        String filtre = null;
                        fieldsIni.add(parts[0]);
                        for (int i = 1; i < parts.length; i++) {
                            filtre = mapFiltres.get(parts[0]);
                            filtre = parts[i];
                            //System.out.println("ajout  ::" +parts[0]+ ", " +parts[i]+ ".");
                            mapFiltres.put(parts[0], filtre);
                            //dumpMap();
                            // TODO : utiliser les regex pour le filtre
                        }
                    } else {
                        //contents.append(System.getProperty("line.separator"));
                        fieldsIni.add(line);
                    }
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void dumpMap() {
        System.out.println("dumpMap");
        for (String key : mapFiltres.keySet()) {
            System.out.println("key " + key + ", val : " + mapFiltres.get(key) + " .");
        }
    }

    private static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    /**
     * lire le fileSrc et retourne un array list de string avec les colonnes
     * utiles
     */
    public ArrayList<String> readInList() {

        ArrayList<String> dataOut = new ArrayList<String>();
//        System.out.println("fieldsIni contient " + fieldsIni.size()+ " elem");
        try {
            // creation du fichier
            //File fileSrc = new File(fileName[FILE_SRC]);
            File fileSrc = createFile(fileName[FILE_SRC]);
            if (!fileSrc.isFile()) {
                throw new FileNotFoundException("FILE_SRC -s incorrect : " + fileName[FILE_SRC]);
            }

            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(fileSrc));
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

            // construction de l'ensemble des n° de colonnes 
            // et construit le titre
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
            dataOut.add(buildLine.toString());

            // lectures des données        
            while ((line = input.readLine()) != null) {
                StringBuilder sbLineOut = new StringBuilder();
                String[] dataLine = line.split(";");

                boolean filtreOk = true; //
                for (int i = 0; i < dataLine.length; i++) {
                    if (colonnes.contains(i)) {
                        String filtreKey = mapFiltres.get(keys[i]);    //System.out.println("keys[" + i + "] (" + keys[i] +")");
                        // presence du filtre ?
                        if (filtreKey != null) {
                            filtreOk = compareFiltre(dataLine[i], filtreKey);
                            if (!filtreOk) {
                                break; // ligne suivante
                            }
                        }
                        sbLineOut.append(dataLine[i]);
                        sbLineOut.append(";");
                    }
                }
                if (filtreOk && sbLineOut.length() > 0) {
                    dataOut.add(sbLineOut.substring(0, sbLineOut.length() - 1)); // enleve le dernier ;
                    // System.out.println("noLine :" + (noLine +2) + ", sbLineOut [" + sbLineOut.substring(0, sbLineOut.length() - 1) + "]");
                }
            }
            input.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Erreur : [" + ex.getMessage() + "]");
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return dataOut;
    }

    public boolean compareFiltre(String value, String filtre) {
        if (filtre.charAt(0) == '!') {
            return !bFiltreContient(value, filtre.substring(1, filtre.length()));
        }
        return bFiltreContient(value, filtre);
    }
    
    public boolean bFiltreContient(String value, String filtre) {
        if (filtre.charAt(0) == '*') {
            if (filtre.charAt(filtre.length() - 1) == '*') {
                return value.contains(filtre.substring(1, filtre.length() - 1));
            }
            return value.contains(filtre.substring(1, filtre.length()));
        }
        if (filtre.charAt(filtre.length() - 1) == '*') {
            return value.contains(filtre.substring(0, filtre.length() - 1));
        }
        return value.contains(filtre);
    }    

    /**
     * Ecrit le arraylist dans un fichier
     *
     * @return true si le fichier est créé
     */
    public boolean writeToFile(ArrayList<String> data) {
        boolean result = false;
        // System.out.println("writeToFile : fileOut ["+fileOut+"]");
        if (fileName[FILE_OUT] == null) {

            fileName[FILE_OUT] = fileName[FILE_SRC];
            int pos = fileName[FILE_SRC].lastIndexOf("\\", fileName[FILE_SRC].length());
            if (pos > 0) {
                fileName[FILE_OUT] = fileName[FILE_SRC].substring(0, pos) + "\\out.csv";
            }
            //System.out.println("pos :" + pos);
            //System.out.println("fileOutName :" + fileName[FILE_OUT]);

        }
        try {
           // File fileOut = new File(fileName[FILE_OUT]);
            File fileOut = createFile(fileName[FILE_OUT]);

            FileWriter fileWriter = new FileWriter(fileOut);  // Assume default encoding.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // Always wrap FileWriter in BufferedWriter.
            try {
                for (String line : data) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    //System.out.println(noLine +", "+line);
                    //noLine++;
                }
                result = true;

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                bufferedWriter.close();
                fileWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * lance les operations pour lire le source et ecrire le fichier en sortie
     */
    public void reduitColonnes() {
        fillIni(); // rempli le HashSet avec fileini

        if (writeToFile(readInList())) {
            System.out.println("Fichier [" + fileName[FILE_OUT] + "] cree.");
        } else {
            System.out.println("Erreur de creation du fichier [" + fileName[FILE_OUT] + "].");
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
        str.append("-i fichierInit.ini : indiquez les noms des champs (colonnes) à conserver\n");
        str.append("il est possible d'ajouter un filtre par ligne, apres le nom du champ.\n");
        str.append("Exemple : CHAMP1;*LOL* pour obtenir les données qui contiennent LOL dans le CHAMP1.\n");
        str.append("-o fichierOut : nom du fichier de sortie\n");
        str.append("-s fichierSource : nom du fichier du fichier source\n");
        str.append("\n");
        str.append("Utilisation: java -jar \"reduitFic.jar\" -i fileini.ini -o fileout.csv -s filesrc.csv \n");

        System.out.println(str.toString());
    }

    private void afficheVersion() {
        System.out.println(version);
    }
}
