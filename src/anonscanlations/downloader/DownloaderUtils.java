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

import org.yaml.snakeyaml.*;

import anonscanlations.downloader.ui.*;

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
        URL u = new URL(url);

        BufferedReader stream = new BufferedReader(new InputStreamReader(u.openStream(), encoding));

	String string = "", line;

	while((line = stream.readLine()) != null)
	    string += line;

        return(string);
    }

    public static byte[] downloadByteArray(URL url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(null);

        InputStream in = conn.getInputStream();
        byte[] buf = new byte[conn.getContentLength()];
        if(in.read(buf) == -1)
            return(null);
        return(buf);
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

    public static SaveData readYAML(String file) throws IOException
    {
        return(readYAML(new FileInputStream(file)));
    }

    public static SaveData readYAML(InputStream stream) throws IOException
    {
        SaveData data = new SaveData();

        ArrayList<Magazine> magazines = new ArrayList<Magazine>();

        Yaml yaml = new Yaml();
        Object obj = yaml.load(stream);
        
        Map<String, Object> root = (Map<String, Object>)obj;
        for(Map.Entry<String, Object> magEntry : root.entrySet())
        {
            if(magEntry.getKey().equals("date"))
            {
                data.setDate((Date)magEntry.getValue());
                continue;
            }

            // not a date
            if(magEntry.getValue() instanceof Map)
            {
                Map<String, Object> magMap = (Map<String, Object>)magEntry.getValue();
                Class c = findClass(magMap);
                if(c == null)
                    continue;
                Constructor cons = findConstructor(c);
                if(cons == null)
                    continue;
                Magazine mag = (Magazine)newInstance(cons);
                mag.importVars(magMap);
                if(mag == null)
                    continue;

                buildMagazine(mag, magMap);
                magazines.add(mag);
            }
        }

        data.setMagazines(magazines);

        return(data);
    }

    private static void buildMagazine(Magazine mag, Map<String, Object> magMap)
    {
        for(Map.Entry<String, Object> seriesEntry : magMap.entrySet())
        {
            if(seriesEntry.getValue() instanceof Map)
            {
                Map<String, Object> seriesMap = (Map<String, Object>)seriesEntry.getValue();
                Class c = findClass(seriesMap);
                if(c == null)
                    continue;
                Constructor cons = findConstructor(c);
                if(cons == null)
                    continue;
                Series series = (Series)newInstance(cons);
                series.setMagazine(mag);
                series.importVars(seriesMap);
                if(series == null)
                    continue;

                buildSeries(series, seriesMap);
                mag.addSeries(series);
            }
        }
    }

    private static void buildSeries(Series series, Map<String, Object> seriesMap)
    {
        for(Map.Entry<String, Object> chapterEntry : seriesMap.entrySet())
        {
            if(chapterEntry.getValue() instanceof Map)
            {
                Map<String, Object> chapterMap = (Map<String, Object>)chapterEntry.getValue();
                Class c = findClass(chapterMap);
                if(c == null)
                    continue;
                Constructor cons = findConstructor(c);
                if(cons == null)
                    continue;
                Chapter chapter = (Chapter)newInstance(cons);
                chapter.importVars(chapterMap);
                chapter.setSeries(series);
                if(chapter == null)
                    continue;

                series.addChapter(chapter);
            }
        }
    }

    private static Class findClass(Map<String, Object> map)
    {
        Class c = null;
        try
        {
            Object name = map.get("class");
            if(name == null)
                return(null);
            c = Class.forName((String)name);
        }
        catch(ClassNotFoundException cnfe)
        {
            DownloaderUtils.error("Class not found", cnfe, false);
        }
        return(c);
    }

    private static Constructor findConstructor(Class c, Class... paramTypes)
    {
        Constructor constructor = null;
        try
        {
            constructor = c.getConstructor(paramTypes);
        }
        catch(NoSuchMethodException nsme)
        {
            DownloaderUtils.error("Constructor not found", nsme, false);
        }
        return(constructor);
    }

    private static Object newInstance(Constructor cons, Object... param)
    {
        Object obj = null;
        try
        {
            obj = cons.newInstance(param);
        }
        catch(Exception e)
        {
            DownloaderUtils.error("couldn't construct new object", e, false);
        }
        return(obj);
    }

    public static void refreshFromServer(DownloaderWindow w)
    {
        final DownloaderWindow window = w;

        window.setTreeState(false);
        Thread serverRetreiver = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    URL object = new URL("https://dl.dropbox.com/u/6792608/" + Downloader.FILE);

                    SaveData data = DownloaderUtils.readYAML(object.openStream());

                    window.addSaveData(data);
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("couldn't retreive data from server", e, false);
                }
                finally
                {
                    window.setTreeState(true);
                }
            }
        };
        serverRetreiver.start();
    }
}
