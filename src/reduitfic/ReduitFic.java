package reduitfic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Reduit les colonnes d'un fichier csv
 *
 * @author Marc
 *
 * utilisation : java -jar "reduitFic.jar" -ifileini.ini -ofileout.csv
 * -sfilesrc.csv java -jar "F:\Zone Sauvegarde\Documents\Documents
 * Marc\MesDocuments\NetBeansProjects\reduitFic\dist\reduitFic.jar" -h java -jar
 * "F:\Zone Sauvegarde\Documents\Documents
 * Marc\MesDocuments\NetBeansProjects\reduitFic\dist\reduitFic.jar" "-i F:\Zone
 * Sauvegarde\Documents\Documents Marc\C3P
 * files\extrData\extrRsurP\iniETIallCol.txt" "-s F:\Zone
 * Sauvegarde\Documents\Documents Marc\C3P
 * files\extrData\extrRsurP\ETI3_J15_151015_064743.csv" java -jar "F:\Zone
 * Sauvegarde\Documents\Documents
 * Marc\MesDocuments\NetBeansProjects\reduitFic\dist\reduitFic.jar" "-i
 * iniETIallCol.txt" "-s ETI3_J15_151015_064743.csv"
 *
 * java -jar "F:\Zone Sauvegarde\Documents\Documents
 * Marc\MesDocuments\NetBeansProjects\reduitFic\dist\reduitFic.jar"
 * -iiniETIallCol.txt -sETI3_J15_151015_064743.csv
 */
public final class ReduitFic {

    // parametres pour les options
    Options options = null;
    CommandLineParser parser = null;
    CommandLine cmd = null;
    HelpFormatter formatter = new HelpFormatter();

    public final void initOptions() {
        options = new Options();
        options.addOption("h", "help", false, "Affiche l'aide");
        options.addOption("ver", "version", false, "numero de version du jar");
        
        
        options.addOption("s", "src", true, "fichier source (reference)");
        options.addOption("i", "ini", true, "fichier init des colonnes a conserver");
        options.addOption("o", "fileout", true, "nom du fichier de sortie");
        
    }    
    
    
    // actions possibles
    static public int ACTION_VERSION = 0;
    static public int ACTION_HELP = 1;
    static public int ACTION_REDUIT = 2;

    String version = "1.5";

    String[] fileNames;  // nom des fichiers
    static private int FILE_INI = 0;
    static private int FILE_SRC = 1;
    static private int FILE_OUT = 2;

    int action; // action choisie

    // contient les noms des champs
    HashSet<String> fieldsIni = new HashSet<String>();
    // filtre associées aux colonnes
    // key : nom de la colonne, value : filtre associé
    HashMap<String, String> mapFiltres = new HashMap<String, String>();

    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    /**
     * création de l'instance. Initialise l'action a mener
     *
     * @param args
     */
    public ReduitFic(String[] args) {
        
        initOptions();
                
        fileNames = new String[3];
        if (traiteCmd(args)) {
            reduitColonnes();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ReduitFic FR = new ReduitFic(args);
    }

        /**
     * recupere les options passees en argument.
     * @param args 
     * @return  true : continue l'execution, false arret.
     */
    public final boolean traiteCmd(String[] args) {
        parser = new DefaultParser();
        //System.err.println("args 01 : " + args[0] + ".");
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            System.out.println("Unexpected exception:" + ex.getMessage());
        }

        if (cmd.hasOption("h")) {
            afficheHelp();
            return false;
        }
        
        if (cmd.hasOption("ver")) {
            System.out.println("reduitFic version : "+version);
            return false;
        }
 
        //------------------------------------------
        // source
        if (!cmd.hasOption("s")) {
            System.err.println("Erreur : fichier source invalide.");
        }
        else {
            fileNames[FILE_SRC] = cmd.getOptionValue("s").trim();
        }
        
        if (!cmd.hasOption("i")) {
            System.err.println("Erreur : fichier source invalide.");
        }
        else {
            fileNames[FILE_INI] = cmd.getOptionValue("i").trim();
        }  
        if (cmd.hasOption("o")) {
           action = ACTION_REDUIT;
           fileNames[FILE_OUT] = cmd.getOptionValue("o").trim();
        }
        return true;
    }
    

    /**
     * cree le fichier et ajoute le rep courant si necessaire
     *
     * @param name nom du fichier a creer
     * @return le fichier
     */
    protected File createFile(String name) {
        //System.err.println("createFile [" + name + "]");
        // si le nom ne contient des "\" ajoute le rep courant
        if (!name.contains("\\")) {
            name = System.getProperty("user.dir") + "\\" + name;
        }
        //System.err.println("path [" + name + "]");
        return new File(name);
    }

    /**
     * rempli le HashSet avec fileini
     */
    public void fillIni() throws java.io.FileNotFoundException {
        // StringBuilder contents = new StringBuilder();
        try {
            // creation du fichier
            //File fileIni = new File(fileNames[FILE_INI]);
            File fileIni = createFile(fileNames[FILE_INI]);
            if (!fileIni.isFile()) {
                throw new java.io.FileNotFoundException();// "fileIn -i incorrect : " + fileNames[FILE_INI]);
            }
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader( //new FileReader(fileIni));
                                        new InputStreamReader(
                                            new FileInputStream(fileIni), "UTF8"));
            try {
                String line = null; //not declared within while loop
                while ((line = input.readLine()) != null) {
                    // rempli les filtres pour chaque colonne
                    String[] parts = line.split("[;]");
                   // System.out.println("fillIni :: line :[" + line + "] parts :" +  parts.length );
                    fieldsIni.add(parts[0]);
                    if (parts.length > 1) {
                        int ps = line.indexOf(";") + 1;
                        String filtre = line.substring(ps, line.length());
                        //System.out.println("ajout filtre  [" + filtre + "]");
                        mapFiltres.put(parts[0], filtre);   // ajoute les filtres dans la map
                    }
                }
            } finally {
                input.close();
            }
        } catch (FileNotFoundException e) {
            throw new java.io.FileNotFoundException();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
     * lire le fileSrc et retourne la liste des données .
     *
     * @return un array list de string avec les colonnes utiles
     */
    public ArrayList<String> readInList() {
        ArrayList<String> dataOut = new ArrayList<String>();
//        System.out.println("fieldsIni contient " + fieldsIni.size()+ " elem");
        try {
            // creation du fichier
            File fileSrc = createFile(fileNames[FILE_SRC]);
            if (!fileSrc.isFile()) {
                throw new FileNotFoundException("FILE_SRC -s incorrect : " + fileNames[FILE_SRC]);
            }
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(fileSrc));
            // --- construction de la ligne titre ----
            StringBuilder buildTitre = new StringBuilder();
            // lecture des titres
            String titresSrc = null;
            String[] keys = null;
            if ((titresSrc = input.readLine()) != null) {
                keys = titresSrc.split("[;]");
            }
            if (keys == null) {
                throw new IllegalArgumentException("le fichier source ne contient pas d'entete");
            }
            // construction de l'ensemble des n° de colonnes 
            // et construit le titre
            TreeSet<Integer> colonnes = new TreeSet<Integer>();
            for (int i = 0; i < keys.length - 1; i++) {
                if (fieldsIni.contains(keys[i])) {
                    colonnes.add(i);
                    buildTitre.append(keys[i] + ";"); // ecriture des titres
                    //System.out.println("colonne add keys["+ i + "] : " + keys[i] );
                }
            }
            if (fieldsIni.contains(keys[keys.length - 1])) {
                colonnes.add(keys.length - 1);
                buildTitre.append(keys[keys.length - 1]); // ecriture du dernier champ des titres
            }
            dataOut.add(buildTitre.toString());
            // --- fin de la construction de la ligne titre ----

            // lectures des données      
            String line = null; //
            while ((line = input.readLine()) != null) {
                String nextLine = readLine(line, colonnes, keys);  //System.out.println("nextLine [" + nextLine + "].");
                if (nextLine != null) {
                    dataOut.add(nextLine);
                }
            }
            input.close();
        } catch (FileNotFoundException ex) {
            System.err.println("Erreur fichier source introuvale : [" + fileNames[FILE_SRC] + "]");
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        // System.out.println("long dataOut " + dataOut.size());
        return dataOut;
    }

    /**
     * Parcours chaque colonnne d'une ligne et applique le filtre associé.
     *
     * @param line String, la ligne
     * @param colonnes HashSet<Integer>, contient les colonnes utiles
     * @param keys String[], noms des colonnes
     * @return
     */
    public String readLine(String line, TreeSet<Integer> colonnes, String[] keys) {
        StringBuilder sbLineOut = new StringBuilder();
        String[] dataLine = line.split("[;]", keys.length);
        // corrige le fichier d'entrée s'il possede moins de colonne que les colonnes titres
        if (dataLine.length < colonnes.size()) {
            for (int i = dataLine.length; i < keys.length; i++) {
                line += ";";
            }
            dataLine = line.split("[;]", keys.length);
        }
        for (int col : colonnes) {
            //System.out.println("keys["+ col + "]: ("+ keys[col] + ")");
            // teste si donnée non valide : exemple ;;;;;;;;;;;;;;;;;;;;;;;
//            try {
//                String champ = dataLine[col];
//            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
//                System.err.println("ArrayIndexOutOfBoundsException  line at " + col + " :(" + line + ") parts : " + dataLine.length);
//                //System.out.println("ArrayIndexOutOfBoundsException dataLine a ["+ (col -1)+ "]: keys("+keys[col-1]+") data : ("+ dataLine[col-1]+")"); 
//                //System.out.println(dataLine[col]);
//                return null;
//            }
            String filtreKey = mapFiltres.get(keys[col]);
            // filtre existant mais donnée non valide 
            if (filtreKey != null) {
                //System.out.println("test de compareFiltre dataLine["+ col + "]: ("+ dataLine[col] + ") et ("+ filtreKey +")  "+ line);
                if (!compareFiltre(dataLine[col], filtreKey)) {
                    //System.out.println("echec de compareFiltre dataLine["+ col + "]: ("+ dataLine[col] + ") et ("+ filtreKey +")  "+ line);
                    return null;
                }
            }
            // filtre ok ou pas de filtre pour la colonne
            // System.out.println("dataLine : ("+ dataLine.length + ")");
            //System.out.println("dataLine["+ col + "]: ("+ dataLine[col] + ")");
            sbLineOut.append(dataLine[col]);
            sbLineOut.append(";");
            //System.out.println("sbLineOut : ("+ sbLineOut.toString().length() + ")");
        }
        //System.out.println("sbLineOut : ("+ sbLineOut.substring(0, sbLineOut.length() - 1).toString() + ")");
        String str = sbLineOut.substring(0, sbLineOut.length() - 1);  // enleve le dernier ";"
//        if (countPv(str) == str.length()) { // enleve les lignes vides (remplies de ; )
//            return null;
//        }
        if (isLigneVide(str)) { return null; }  // retire les lignes remplies uniquement de ";"
        return str;
    }

    /**
     * Compte le nombre de ; dans la Chaine.
     *
     * @param line Chaine
     * @return int
     */
    private int countPv(String line) {
        Pattern p = Pattern.compile("[;]");
        Matcher m = p.matcher(line);
        int countpv = 0;
        while (m.find()) {
            countpv++;
        }
        return countpv;
    }
    
    /**
     * True si la ligne n'est composée que de ;
     * @param line
     * @return 
     */
    private boolean isLigneVide(String line) {
        int i =0;
        while (i <line.length()) {
            if (line.charAt(i) != ';') return false;
            i++;
        }
        return true;
    }

    public static boolean compareFiltre(String value, String filtre) {
        //System.out.println("value : (" + value + ") : filtre{" + filtre + "} ");
        String[] parts = filtre.split(";");
        if (parts.length > 1) {
            // applique les filtres OU
            boolean result = false;
            for (int i = 0; i < parts.length; i++) {
                //System.out.println("value : ("+ value + ") : filtre{"+filtre+"} : ("+ parts[i] + ") :  return : " + setFiltreET(value, parts[i]));
                result |= setFiltreET(value, parts[i]);
            }
            return result;
        } else {
            return setFiltreET(value, parts[0]);
        }
    }

    /**
     * Pour chaque condition ou, applique les filtres ET
     * @param value donnée
     * @param filtre filtre entre 2 ;
     * @return true ou false
     */
    public static boolean  setFiltreET(String value, String filtre) {
        // applique les filtres ET
        boolean resultET = true;
        String[] partsET = filtre.split("&");
        for (int e = 0; e < partsET.length; e++) {
            //System.out.println("value : (" + value + ") : filtre{" + filtre + "} : (" + partsET[e] + ")");// :  return : " + baseFiltre(value, partsET[e]));
            resultET &= baseFiltre(value, partsET[e]);
        }
        return resultET;
    }

    /**
     * Filtre de base
     *  ! : non
     *  * : joker uniquement en *blabla ou blabla* ou *blabla*
     *       bla*bla ne donne rien
     *  # : uniquement les vides
     *  !#: tout sauf les vides
     * 
     * @param value
     * @param filtre
     * @return 
     */
    public static boolean baseFiltre(String value, String filtre) {
        if (filtre.length() == 1 && filtre.charAt(0) == '*') {
            return true;
        } // tous les champs
        // vide: #  et non vide !#
        if (filtre.length() == 1 && filtre.charAt(0) == '#') {
            return value.length() == 0;
        } // vide
        if (filtre.length() == 2 && filtre.equals("!#")) {
            return value.length() > 0;
        } // non vide : tous    

        if (filtre.charAt(0) == '!') {
            return !bFiltreContient(value, filtre.substring(1, filtre.length()));
        }
        return bFiltreContient(value, filtre);
    }

    /**
     * Teste si une valeur contient le filtre
     * "*blabla"  ou "blabla*" ou "*blabla*" ou "blabla"
     * @param value
     * @param filtre '*' caractere joker
     * @return 
     */
    public static boolean bFiltreContient(String value, String filtre) {
        if (filtre.charAt(0) == '*') {
            if (filtre.charAt(filtre.length() - 1) == '*') {
                return value.contains(filtre.substring(1, filtre.length() - 1));
            }
            return value.endsWith(filtre.substring(1, filtre.length()));
        }
        if (filtre.charAt(filtre.length() - 1) == '*') {
            return value.startsWith(filtre.substring(0, filtre.length() - 1));
        }
        // System.out.println("value["+ value+ "] ? filtre ("+ filtre + ")");
        return value.equals(filtre);
    }

    /**
     * Ecrit le arraylist dans un fichier
     *
     * @param data
     * @return true si le fichier est créé
     */
    public boolean writeToFile(ArrayList<String> data) {
        boolean result = false;
        if (data == null) {
            return false;
        }
        if (data.size() < 1) {
            return false;
        }
        // System.out.println("writeToFile : fileOut ["+fileOut+"]");
        if (fileNames[FILE_OUT] == null) {

            fileNames[FILE_OUT] = fileNames[FILE_SRC];
            int pos = fileNames[FILE_SRC].lastIndexOf("\\", fileNames[FILE_SRC].length());
            if (pos > 0) {
                fileNames[FILE_OUT] = fileNames[FILE_SRC].substring(0, pos) + "\\out.csv";
            } else {
                fileNames[FILE_OUT] = "out.csv";
            }
            //System.out.println("pos :" + pos);
            //System.out.println("fileOutName :" + fileNames[FILE_OUT]);

        }
        try {
            // File fileOut = new File(fileNames[FILE_OUT]);
            File fileOut = createFile(fileNames[FILE_OUT]);

            FileWriter fileWriter = new FileWriter(fileOut);  // Assume default encoding.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter); // Always wrap FileWriter in BufferedWriter.
            try {
                for (String line : data) {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                    //System.out.println("write line :["+line + "]");
                    //noLine++;
                }
                result = true;

            } catch (IOException ex) {
                System.out.println("IOException1 :" + ex.getMessage());
                ex.printStackTrace();
            } finally {
                bufferedWriter.close();
                fileWriter.close();
            }
        } catch (IOException ex) {
            System.out.println("IOException :" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * lance les operations pour lire le source et ecrire le fichier en sortie
     */
    public void reduitColonnes() {
        //System.out.println("reduitColonnes");
        try {
            fillIni(); // rempli le HashSet avec fileini
        } catch (java.io.FileNotFoundException e) {
            System.out.println("Fichier ini introuvable : [" + fileNames[FILE_INI] + "]." + e.getMessage());
            return;
        }

        if (writeToFile(readInList())) {
            System.out.println("Fichier [" + fileNames[FILE_OUT] + "] crée.");
        } else {
            System.out.println("Erreur de création du fichier : [" + fileNames[FILE_OUT] + "].");
        }
    }

    // --------------------
    /*
     * aide en ligne de commande   
     */
    public void afficheHelp() {

        StringBuilder str = new StringBuilder();
        str.append("Aide : reduit le nombre de colonne d'un fichier.");
        str.append(" Ajouter des filtres à la suite du nom du champ (condition OU).\n");
        str.append(" Exemple :\n  NOM CHAMP;*DGT;*FTH*  pour obtenir les données qui finissent par DGT\n");
        str.append("  OU qui contiennent FTH.\n");
        str.append("  !*TOTO : ne commence pas par TOTO\n");
        str.append("  # : uniquement les valeurs vides\n");
        str.append("  !# : uniquement les valeurs non vides\n");
        str.append("  & : pour chaque filtre il est possible d'appliquer une condition ET logique\n");
        str.append(" Exemple :\n  NOM CHAMP;!1*&!2*;*FTH*  pour obtenir les données qui ne commencent pas par 1 ET par 2; OU qui contienne FTH\n");
        str.append("\n-o fichierOut :\n nom du fichier de sortie\n");
        str.append(" si ce parametre est omis le nom du fichier de sortie sera \"out.csv\"\n");
        str.append(" et sera place dans le repertoire de la source.");
        str.append("\n");
        str.append("\n Exemple d'utilisation:\n java -jar \"reduitFic.jar\" \"-i fileini.ini\" \"-s filesrc.csv\" \"-o fileout.csv\" \n");
        
        formatter.printHelp(str.toString() + "\n", options);
    }

}
