/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/** Random utility functions
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

    public static synchronized void debug(String message)
    {
        System.out.println("DEBUG: " + message);
        addToLog(message);
    }
    public static synchronized void error(String message, Exception e, boolean fatal)
    {
        String msg = (fatal ? "" : "NON-") + "FATAL ERROR: " + message;
        System.err.println(msg);
        addToLog("!!!!! " + msg);
        addException(msg, e);
        if(fatal)
            System.exit(1);
    }
    public static synchronized void errorGUI(String message, Exception e, boolean fatal)
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

    private static synchronized void addToLog(String message)
    {
        LOG.add(message);
        try
        {
            if(LOGEDITOR != null)
                LOGEDITOR.setText(LOGEDITOR.getText() + message + '\n');
        }
        catch(Exception e)
        {
            // Sometimes the log just isn't ready
        }
    }

    private static synchronized boolean addException(String msg, Exception e)
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

    public static void safeWrite(byte[] bytes, File f) throws IOException
    {
        safeWrite(bytes, 0, bytes.length, f);
    }
    public static void safeWrite(byte[] bytes, int off, int len, File f) throws IOException
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(f);
            fos.write(bytes, off, len);
        }
        finally
        {
            if(fos != null)
                fos.close();
        }
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
        Connection.Response response = Jsoup.connect(url.toString()).followRedirects(true).execute();
        return(response.url());
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
                DownloaderUtils.errorGUI("Couldn't browse to page: " + url, e, false);
            }
        }
    }
    
    public static void openDir(File f)
    {
        if(!f.isDirectory())
            return;
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if(desktop.isSupported(java.awt.Desktop.Action.OPEN))
        {
            try
            {
                desktop.open(f);
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("Couldn't open dir: " + f, e, false);
            }
        }
    }

    public static HashMap<String, String> getQueryMap(java.net.URL url)
            throws UnsupportedEncodingException
    {
        return(getQueryMapFromQueryString(URLDecoder.decode(url.getQuery(), "UTF-8")));
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
    
    private static final String BAD_CHARS ="[\\\\/:*?\"<>\\|]";
    
    public static File fileName(File directory, String title, int page, String ext)
    {
        return(new File(directory, String.format("%s_%03d.%s", sanitizeFileName(title), page, ext)));
    }

    public static File fileName(File directory, int page, String ext)
    {
        return(new File(directory, String.format("%03d.%s", page, ext)));
    }
    
    public static String sanitizeFileName(String fname)
    {
        return(fname.replace(' ', '_').replaceAll(BAD_CHARS, ""));
    }
    
    public static void tryMkdirs(File directory) throws Exception
    {
        if(directory.exists())
            if(!directory.isDirectory())
                directory.delete();
        if(!directory.exists())
            if(!directory.mkdirs())
                throw new Exception("Unable to create new directory");
    }
    
    public static String pageOutOf(int i, int start, int total)
    {
        return(String.format("Page %d out of %d", i - start + 1, total));
    }
    
    public static final SAXException DONE = new SAXException("Sax parsing done");
}
