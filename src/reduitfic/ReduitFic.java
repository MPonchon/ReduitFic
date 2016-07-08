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
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ReduitFic {

    // actions possibles
    static public int ACTION_VERSION = 0;
    static public int ACTION_HELP = 1;
    static public int ACTION_REDUIT = 2;

    String version = "ReduitFic v1.4";
    String DescritonVersion = "Modification parametres.";

    String[] fileName;  // nom des fichiers
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
            //File fileIni = new File(fileName[FILE_INI]);
            File fileIni = createFile(fileName[FILE_INI]);
            if (!fileIni.isFile()) {
                throw new java.io.FileNotFoundException();// "fileIn -i incorrect : " + fileName[FILE_INI]);
            }
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(fileIni));
            try {
                String line = null; //not declared within while loop
                while ((line = input.readLine()) != null) {
                    // rempli les filtres pour chaque colonne
                    String[] parts = line.split("[;]");
                    //System.out.println("fillIni :: line :[" + line + "] parts :" +  parts.length );
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
            File fileSrc = createFile(fileName[FILE_SRC]);
            if (!fileSrc.isFile()) {
                throw new FileNotFoundException("FILE_SRC -s incorrect : " + fileName[FILE_SRC]);
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
            System.err.println("Erreur fichier source introuvale : [" + fileName[FILE_SRC] + "]");
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

    public boolean compareFiltre(String value, String filtre) {
        //System.out.println("value : (" + value + ") : filtre{" + filtre + "} ");
        String[] parts = filtre.split(";");
        if (parts.length > 1) {
            // applique les filtres OU
            boolean result = false;
            for (int i = 0; i < parts.length; i++) {
                //System.out.println("value : ("+ value + ") : filtre{"+filtre+"} : ("+ parts[i] + ") :  return : " + testFiltre(value, parts[i]));
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
    public boolean setFiltreET(String value, String filtre) {
        // applique les filtres ET
        boolean resultET = true;
        String[] partsET = filtre.split("&");
        for (int e = 0; e < partsET.length; e++) {
            //System.out.println("value : (" + value + ") : filtre{" + filtre + "} : (" + partsET[e] + ") :  return : " + testFiltre(value, partsET[e]));
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
    public boolean baseFiltre(String value, String filtre) {
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

    public boolean bFiltreContient(String value, String filtre) {
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
        if (fileName[FILE_OUT] == null) {

            fileName[FILE_OUT] = fileName[FILE_SRC];
            int pos = fileName[FILE_SRC].lastIndexOf("\\", fileName[FILE_SRC].length());
            if (pos > 0) {
                fileName[FILE_OUT] = fileName[FILE_SRC].substring(0, pos) + "\\out.csv";
            } else {
                fileName[FILE_OUT] = "out.csv";
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
            System.out.println("Fichier introuvable : [" + fileName[FILE_INI] + "]." + e.getMessage());
            return;
        }

        if (writeToFile(readInList())) {
            System.out.println("Fichier [" + fileName[FILE_OUT] + "] crée.");
        } else {
            System.out.println("Erreur de création du fichier : [" + fileName[FILE_OUT] + "].");
        }
    }

    // --------------------
    /*
     * aide en ligne de commande   
     */
    public void AfficheHelp() {
        System.out.println();
        afficheVersion();
        System.out.println("------------------");
        System.out.println("Aide : reduit le nombre de colonne d'un fichier.");
        StringBuilder str = new StringBuilder();
        str.append("options :\n");
        str.append("\n-s fichierSource :\n nom du fichier du fichier source\n");
        str.append("\n-i fichierInit.ini :\n indiquez les noms des champs (colonnes) à conserver\n");
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

        System.out.println(str.toString());
    }

    private void afficheVersion() {
        System.out.println(version);
    }
}
