package CSRfidJavaMultipleReader;

import java.awt.*;
import java.io.*;
import javax.swing.JFileChooser;
import Netfinder.*;

/**
 *
 */
public class Main {

    // <editor-fold defaultstate="collapsed" desc="Variable">
    public static String demeAppVer = "1.0.18";
    public static NetFinder netfinder;
    public static String applicationSettings = "";
    public static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    public static String[] ipAddress;
    public static String[] macAddress;
    public static String[] deviceName;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Init">
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFileChooser fr = new JFileChooser();
        javax.swing.filechooser.FileSystemView fw = fr.getFileSystemView();
        Main.applicationSettings = fw.getDefaultDirectory()+"\\CSLLLReaderLog";
        
        File theDir = new File(Main.applicationSettings);
        if (!theDir.exists()) {
          try{
              theDir.mkdir();
           } catch(SecurityException se){
              //handle it
           }
        }
        
        Main.applicationSettings += "\\";
        
        new NetFinderFrame().setVisible(true);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setting">
    public static void SaveSettings()
    {
    }

    public static void LoadSettings()
    {
    }
    // </editor-fold>
}
