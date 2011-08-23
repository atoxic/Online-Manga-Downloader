/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import javax.swing.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public class DownloaderUtils
{
    public static final boolean SPILL_GUTS = true;

    public static final ArrayList<Exception> ERRORS = new ArrayList<Exception>();

    public static void debug(String message)
    {
        System.out.println("DEBUG: " + message);
    }
    public static void error(String message, Exception e, boolean fatal)
    {
        String msg = (fatal ? "" : "NON-") + "FATAL ERROR: " + message;
        System.err.println(msg);
        addException(msg, e);
        if(fatal)
            System.exit(1);
    }
    public static void errorGUI(String message, Exception e, boolean fatal)
    {
        String msg = (fatal ? "" : "NON-") + "FATAL ERROR: " + message;
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        addException(msg, e);
        if(fatal)
            System.exit(1);
    }

    private static void addException(String msg, Exception e)
    {
        ERRORS.add(new Exception(msg, e));
        if(SPILL_GUTS)
        {
            System.err.println("LOGGER: Exception added");
            e.printStackTrace();
        }
    }

    // Source http://www.rgagnon.com/javadetails/java-0307.html
    private static final HashMap<String,String> htmlEntities = new HashMap<String,String>();
    static
    {
        htmlEntities.put("&lt;","<")    ;   htmlEntities.put("&gt;",">");
        htmlEntities.put("&amp;","&")   ;   htmlEntities.put("&quot;","\"");
        htmlEntities.put("&agrave;","à");   htmlEntities.put("&Agrave;","À");
        htmlEntities.put("&acirc;","â") ;   htmlEntities.put("&auml;","ä");
        htmlEntities.put("&Auml;","Ä")  ;   htmlEntities.put("&Acirc;","Â");
        htmlEntities.put("&aring;","å") ;   htmlEntities.put("&Aring;","Å");
        htmlEntities.put("&aelig;","æ") ;   htmlEntities.put("&AElig;","Æ" );
        htmlEntities.put("&ccedil;","ç");   htmlEntities.put("&Ccedil;","Ç");
        htmlEntities.put("&eacute;","é");   htmlEntities.put("&Eacute;","É" );
        htmlEntities.put("&egrave;","è");   htmlEntities.put("&Egrave;","È");
        htmlEntities.put("&ecirc;","ê") ;   htmlEntities.put("&Ecirc;","Ê");
        htmlEntities.put("&euml;","ë")  ;   htmlEntities.put("&Euml;","Ë");
        htmlEntities.put("&iuml;","ï")  ;   htmlEntities.put("&Iuml;","Ï");
        htmlEntities.put("&ocirc;","ô") ;   htmlEntities.put("&Ocirc;","Ô");
        htmlEntities.put("&ouml;","ö")  ;   htmlEntities.put("&Ouml;","Ö");
        htmlEntities.put("&oslash;","ø");   htmlEntities.put("&Oslash;","Ø");
        htmlEntities.put("&szlig;","ß") ;   htmlEntities.put("&ugrave;","ù");
        htmlEntities.put("&Ugrave;","Ù");   htmlEntities.put("&ucirc;","û");
        htmlEntities.put("&Ucirc;","Û") ;   htmlEntities.put("&uuml;","ü");
        htmlEntities.put("&Uuml;","Ü")  ;   htmlEntities.put("&nbsp;"," ");
        htmlEntities.put("&copy;","\u00a9");
        htmlEntities.put("&reg;","\u00ae");
        htmlEntities.put("&euro;","\u20a0");
    }
    
    public static String unescapeHTML(String source)
    {
        int i, j;

        boolean continueLoop;
        int skip = 0;
        do
        {
            continueLoop = false;
            i = source.indexOf("&", skip);
            if(i > -1)
            {
                j = source.indexOf(";", i);
                if(j > i)
                {
                    String entityToLookFor = source.substring(i, j + 1);
                    String value = htmlEntities.get(entityToLookFor);
                    if (value != null)
                    {
                        source = source.substring(0, i)
                                + value + source.substring(j + 1);
                        continueLoop = true;
                    }
                    else if (value == null)
                    {
                        skip = i+1;
                        continueLoop = true;
                    }
                }
            }
        }
        while(continueLoop);
        return(source);
    }

    private static final StringBuilder SB = new StringBuilder();
    private static final Formatter FORMATTER = new Formatter(SB, Locale.US);
    private static final String BAD_CHARS ="[\\\\/:*?\"<>\\|]";

    public static File fileName(File directory, String title, int page, String ext)
    {
        SB.setLength(0);
        FORMATTER.format("%s_%03d.%s", title, page, ext);
        String saveTitle = SB.toString().replace(' ', '_').replaceAll(BAD_CHARS, "");
        return(new File(directory, saveTitle));
    }
}
