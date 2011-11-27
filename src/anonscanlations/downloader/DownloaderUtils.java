/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 *
 * @author /a/non
 */
public class DownloaderUtils
{
    /* ===============================================
     * ERROR HANDLING
     * ===============================================
     */
    public static final boolean SPILL_GUTS = true;
    public static final HashMap<String, Exception> ERRORS = new HashMap<String, Exception>();
    public static final ArrayList<String> LOG = new ArrayList<String>();
    public static JEditorPane LOGEDITOR = null;

    public static void debug(String message)
    {
        System.out.println("DEBUG: " + message);
        addToLog(message);
    }
    public static void error(String message, Exception e, boolean fatal)
    {
        String msg = (fatal ? "" : "NON-") + "FATAL ERROR: " + message;
        System.err.println(msg);
        addToLog("!!!!! " + msg);
        addException(msg, e);
        if(fatal)
            System.exit(1);
    }
    public static void errorGUI(String message, Exception e, boolean fatal)
    {
        String msg = (fatal ? "" : "NON-") + "FATAL ERROR: " + message;
        addToLog("!!!!! " + msg);
        // don't report it again if it's been reported
        if(addException(msg, e))
        {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(fatal)
            System.exit(1);
    }

    private static void addToLog(String message)
    {
        LOG.add(message);
        if(LOGEDITOR != null)
            LOGEDITOR.setText(LOGEDITOR.getText() + message + '\n');
    }

    private static boolean addException(String msg, Exception e)
    {
        if(ERRORS.containsValue(e))
            return(false);
        ERRORS.put(msg, e);
        if(SPILL_GUTS)
        {
            System.err.println("LOGGER: Exception added");
            e.printStackTrace();
            addToLog(e.getMessage());
            for(StackTraceElement element : e.getStackTrace())
                addToLog(element.toString());
        }
        return(true);
    }
    
    /* ===============================================
     * NETWORKING AND I/O
     * ===============================================
     */
    
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

    public static byte[] readAllBytes(InputStream in) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] array = new byte[1024];
        int len;
        while((len = in.read(array)) != -1)
            bos.write(array, 0, len);
        array = bos.toByteArray();
        bos.close();
        return(array);
    }

    public static String readAllLines(InputStream in, String encoding) throws IOException
    {
        StringBuilder page = new StringBuilder();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding));
        while((line = reader.readLine()) != null)
            page.append(line).append('\n');
        return(page.toString());
    }
    
    public static URL getRedirectURL(URL url) throws IOException
    {
        if(url == null)
            return(null);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        if(conn.getResponseCode() < 300 && conn.getResponseCode() > 302)
            return(url);
        String headerName = null;
        for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            if(headerName.equals("Location"))
            {
                return(new URL(url, conn.getHeaderField(i)));
            }
        }
        return(url);
    }

    public static void browse(java.net.URL url)
    {
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if(desktop.isSupported(java.awt.Desktop.Action.BROWSE))
        {
            try
            {
                desktop.browse(url.toURI());
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("couldn't browse to page: " + url, e, false);
            }
        }
    }

    public static HashMap<String, String> getQueryMap(java.net.URL url)
    {
        return(getQueryMapFromQueryString(url.getQuery()));
    }
    
    public static HashMap<String, String> getQueryMapFromQueryString(String str)
    {
        String[] params = str.split("&");
        HashMap<String, String> map = new HashMap<String, String>();
        for(String param : params)
        {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return(map);
    }

    public static Document makeDocument(String page) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(page));
        Document d = builder.parse(is);

        return(d);
    }
    
    public static void checkHTTP(URL url) throws IOException
    {
        if(!url.getProtocol().equals("http"))
            throw new IOException("Can only use http");
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
    
    public static final SAXException DONE = new SAXException("Sax parsing done");
}
