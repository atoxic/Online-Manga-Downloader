/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.lang.reflect.*;

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

    public static void browse(String url)
    {
        Desktop desktop = Desktop.getDesktop();

        if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
        {
            try
            {
                URI uri = new URI(url);
                desktop.browse(uri);
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("couldn't browse to page: " + url, e, false);
            }
        }
    }

    public static JEditorPane makeHyperlinkLabel(String label)
    {
        JEditorPane labelPane = new JEditorPane("text/html", label);
        labelPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        labelPane.setEditable(false);
        labelPane.setOpaque(false);
        labelPane.setMaximumSize(new Dimension(10000000, labelPane.getPreferredSize().height));
        if(label.startsWith("<html>"))
        {
            labelPane.addHyperlinkListener(new HyperlinkListener()
            {
                public void hyperlinkUpdate(HyperlinkEvent hle)
                {
                    if(HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType()))
                    {
                        browse(hle.getURL().toString());
                    }
                }
            });
        }
        return(labelPane);
    }

    public static String getPage(String url, String encoding) throws IOException
    {
        return(getPage(url, encoding, null));
    }
    public static String getPage(String url, String encoding, String cookies) throws IOException
    {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        if(cookies != null)
            conn.setRequestProperty("Cookie", cookies);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(null);
        BufferedReader stream = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

	String string = "", line;

	while((line = stream.readLine()) != null)
	    string += line;

        stream.close();
        return(string);
    }

    public static boolean downloadFile(URL url, String localFile) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(false);

        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(localFile));

        InputStream in = conn.getInputStream();
        byte[] buf = new byte[1024];
        int read;
        while((read = in.read(buf)) != -1)
            output.write(buf, 0, read);

        in.close();
        output.close();
        return(true);
    }

    public static BufferedImage downloadImage(URL url) throws IOException
    {
        DownloaderUtils.debug("downloading image: " + url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(null);

        InputStream in = conn.getInputStream();

        BufferedImage image = ImageIO.read(in);

        return(image);
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
}
