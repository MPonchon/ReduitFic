/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reduitfic;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marc
 */
public class ReduitFicTest {
    ReduitFic FR;
    
    public ReduitFicTest() {
        /*
        String args[] = new String[3];
        args[0] = "-ifileini.ini";
        args[1] = "-sfilesrc.csv";
        args[2] = "-ofileout.csv";

        FR = new ReduitFic(args);*/
        
    }
    
    @BeforeClass
    public static void setUpClass() {

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

 
    /**
     * Test of init, of class ReduitFic.
     */
   // @Test
    public void testInit() {
        String args[] = new String[3];
        args[0] = "-iFileIni.ini";
        args[1] = "-oFileSrc.txt";
        args[2] = "-sFileOut.csv";

        FR = new ReduitFic(args);
    }
    
    
    //@Test
    public void testPlanif() {
        String args[] = new String[3];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniPlanif.txt3";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\exportOT_20042016_planifiédu19.csv";
        args[2] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\planifOut.csv";
        FR = new ReduitFic(args);
        
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniPlanif.txt";
        FR = new ReduitFic(args);
    }
    
   // @Test
    public void testVersion() {
        String args[] = new String[4];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniPlanif.txt";
        args[1] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\planifOut.csv";
       args[2] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\exportOT_20042016_planifiédu19.csv";
        args[0] = "-v";
        
        FR = new ReduitFic(args);
    }
    
   // @Test
    public void testHelp() {
        String args[] = new String[4];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniPlanif.txt";
        args[1] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\planifOut.csv";
        args[2] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\exportOT_20042016_planifiédu19.csv";
        args[0] = "-h";
        
        FR = new ReduitFic(args);

    }   
    
    
   // @Test
    public void testsrcF15() {
        String args[] = new String[2];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniF15.txt";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\src_testF15.csv";
        //args[1] ="in.csv";
        //args[2] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIout.csv";
        
        FR = new ReduitFic(args);

        ReduitFic.main(args);
    }

    //@Test
    public void testfiltreEC() {
        System.out.println("Test de testfiltreEC");
        String args[] = new String[3];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniEC.txt";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ENCOURS.CSV_160420_043423.csv";
        args[2] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ECout.csv";

        FR = new ReduitFic(args);
        ReduitFic.main(args);
    }
    
    //@Test
    public void testETI() {
        System.out.println("Test de testETI");
        String args[] = new String[2];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniETI.txt";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIsrc_test.csv";
        //args[1] ="in.csv";
        //args[2] = "-oF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIout.csv";
        
        FR = new ReduitFic(args);
        assertEquals(ReduitFic.ACTION_REDUIT, FR.action);
     //   FR.executeAction();
        ReduitFic.main(args);
    }     
    
    
    
    
    //@Test
    public void testFiltreFileIni() {
        System.out.println("Test de testFiltreFileIni");
        String args[] = new String[3];
        args[0] = "-i    F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\initETI_filtre.txt";
        args[1] = "-s F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETI3_J15_151015_064743.csv";
        //args[1] ="in.csv";
        args[2] = "-o   F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIout.csv";
        
        FR = new ReduitFic(args);
        assertEquals(ReduitFic.ACTION_REDUIT, FR.action);
      //  FR.fillIni();
       // FR.readInList();
     //   ReduitFic.main(args);
    }   

    @Test
    public void testSansOut() {
        System.out.println("Test de testSansOut");
        String args[] = new String[2];
        args[0] = "-i F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\iniETIallCol.txt";
        args[1] = "-s F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETI3_J15_151015_064743.csv";
        args[1] = "-s F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETI3_J15.csv";
       // FR = new ReduitFic(args);
       // assertEquals(ReduitFic.ACTION_REDUIT, FR.action);
      //  FR.fillIni();
       // FR.readInList();
        //FR.executeAction();
        ReduitFic.main(args);
    }   

    
   
    /**
     * Test of reduitColonnes method, of class ReduitFic.
     */
    //@Test
    public void testCompareFiltre() {
        System.out.println("Test de testCompareFiltre");
        String chaine = "hello lolo";
        String filtre = "lo";
        
        String args[] = new String[2];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\initETI_filtre.txt";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIsrc_test.csv";        
        FR = new ReduitFic(args);
        
        assertTrue(FR.compareFiltre(chaine, filtre));
        assertTrue(FR.compareFiltre(chaine, "lolo"));
        assertTrue(FR.compareFiltre(chaine, chaine));
        assertTrue(FR.compareFiltre(chaine, "*lolo"));
        assertTrue(FR.compareFiltre(chaine, "hello*"));
        
        assertFalse(FR.compareFiltre(chaine, "lop"));
        assertFalse(FR.compareFiltre(chaine, "lop*"));
        
        assertFalse(FR.compareFiltre(chaine, "!lolo"));
        assertFalse(FR.compareFiltre(chaine, "!*lolo"));
        assertFalse(FR.compareFiltre(chaine, "!hello*"));    
        assertTrue(FR.compareFiltre(chaine, "!loo"));
        assertTrue(FR.compareFiltre(chaine, "!lop*"));
    }

   // @Test
    public void testbFiltreContient() {
        System.out.println("testbFiltreContient");
        
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "*lo*"));
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "*lol"));
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "val*"));
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "*toto*"));
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "value toto lol"));
        assertTrue(ReduitFic.bFiltreContient("value toto lol", "value toto lol*"));
        
        assertFalse(ReduitFic.bFiltreContient("value toto lol", "lol"));
        assertFalse(ReduitFic.bFiltreContient("value toto lol", "lo*l"));
    }
    
    @Test
    public void testbaseFiltre() {
        System.out.println("testbaseFiltre");
        
        assertTrue(ReduitFic.baseFiltre("value toto lol", "!lol"));
        assertTrue(ReduitFic.baseFiltre("value toto lol", "*lo*"));
        assertTrue(ReduitFic.baseFiltre("value toto lol", "*lol"));
        assertTrue(ReduitFic.baseFiltre("value toto lol", "val*"));
        assertTrue(ReduitFic.baseFiltre("value toto lol", "*toto*"));
        assertTrue(ReduitFic.baseFiltre("value toto lol", "value toto lol"));       
        
        assertTrue(ReduitFic.baseFiltre("value toto lol", "!#"));  
        assertFalse(ReduitFic.baseFiltre("value toto lol", "#"));  
        assertFalse(ReduitFic.baseFiltre("", "!#"));  
        assertTrue(ReduitFic.baseFiltre("", "#"));  
        
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!*lol*"));
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!*lo*"));
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!*lol"));
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!val*"));
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!*toto*"));
        assertFalse(ReduitFic.baseFiltre("value toto lol", "!value toto lol"));          
    }
    
    @Test
    public void testsetFiltreET() {
        System.out.println("testsetFiltreET");
        
        assertTrue(ReduitFic.setFiltreET("value toto lol", "*lol&*toto*"));
        assertTrue(ReduitFic.setFiltreET("value toto lol", "*lol&*toto*&value*"));
        assertTrue(ReduitFic.setFiltreET("value toto lol", "*lol&*toto*&value toto lol"));

        assertFalse(ReduitFic.setFiltreET("value toto lol", "*lol&*toto*&value toto lol&non"));

    }
    
    @Test
    public void testcompareFiltre() {
        System.out.println("testcompareFiltre");
        
        assertTrue(ReduitFic.compareFiltre("value toto lol", "*lol&*toto*;*toto*"));
        assertTrue(ReduitFic.compareFiltre("value toto lol", "*lol&*toto*&value*;!#"));
        assertTrue(ReduitFic.compareFiltre("value toto lol", "*lol&*toto*&value toto lol"));

        assertTrue(ReduitFic.compareFiltre("value toto lol", "*lol&*toto*&value toto lol;#"));
        assertFalse(ReduitFic.compareFiltre("value toto lol", "*lol&*toto*&value toto lol&#"));
    }    
    
    
    
    
    //@Test
    public void testCreateFile() {
        System.out.println("Test de testCreateFile");
        
        String args[] = new String[2];
        args[0] = "-iF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\initETI_filtre.txt";
        args[1] = "-sF:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\ETIsrc_test.csv";    
        FR = new ReduitFic(args);
        
        FR.createFile("F:\\Zone Sauvegarde\\Documents\\Documents Marc\\C3P files\\extrData\\extrRsurP\\initETI_filtre.txt");
        FR.createFile("initETI_filtre.txt");
    }
    
}
