/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.net.*;
import java.util.*;

import anonscanlations.downloader.ui.*;
import anonscanlations.downloader.yahoocomic.*;
import anonscanlations.downloader.ganganonline.*;
import anonscanlations.downloader.comichigh.*;
import anonscanlations.downloader.sunday.*;

/**
 *
 * @author /a/non
 */
public class Downloader
{
    public static final String ABOUT = "<html>About Online Manga Downloader<br/>"
            + "PCViewer decrypter and Club Sunday scraper made by Nagato<br/>"
            + "GUI made by /a/nonymous scanlations<br/>"
            + "<a href=\"http://anonscanlations.blogspot.com/\">"
                + "http://anonscanlations.blogspot.com/</a><br/>"
            + "Licensed (lol) under new BSD<br/>";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        try
        {
            DownloadInfoServer.loadAllInfo();
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Couldn't load info on series or magazines", e, true);
        }

        DownloadInfoServer.SITES.put(ComicHighSite.SITE.getName(), ComicHighSite.SITE);
        DownloadInfoServer.SITES.put(SundaySite.SITE.getName(), SundaySite.SITE);
        DownloadInfoServer.SITES.put(GanGanOnlineSite.SITE.getName(), GanGanOnlineSite.SITE);
        DownloadInfoServer.SITES.put(YahooComicSite.SITE.getName(), YahooComicSite.SITE);

        PreferencesManager.initializePrefs();

        if(args.length >= 2 && args[0].equals("--server"))
        {
            server(args[1]);
        }
        else if(args.length == 0)
        {
            client();
        }
        else
        {
            DownloaderUtils.error("Either start with no parameters or start with --server <file to save to>", null, true);
        }
    }

    private static void server(String file)
    {
        DownloaderUtils.debug("running server");

        TreeMap<String, Magazine> magazines =
                new TreeMap<String, Magazine>();

        for(Map.Entry<String, Site> entry : DownloadInfoServer.SITES.entrySet())
        {
            DownloaderUtils.debug("getting magazine: " + entry.getKey());

            TreeMap<String, Magazine> siteMagazines = null;
            try
            {
                siteMagazines = entry.getValue().getMagazines();
            }
            catch(IOException ioe)
            {
                DownloaderUtils.error("Could not get data on magazine \""
                                    + entry.getKey() + "\"", ioe, false);
            }
            magazines.putAll(siteMagazines);
        }

        SaveData data = new SaveData();
        data.setMagazines(magazines);
        data.resetDate();
        try
        {
            data.dumpYAML(file);
        }
        catch(IOException ioe)
        {
            DownloaderUtils.error("Could not save data", ioe, true);
        }
    }

    private static void client()
    {
        DownloaderUtils.debug("running client");

        SaveData data = null;

        try
        {
            data = DownloaderUtils.readYAML("data/manga_download_info.yml");

            final DownloaderWindow window = new DownloaderWindow(data);

            if(PreferencesManager.PREFS.getBoolean(PreferencesManager.KEY_SERVERCHECK, true))
                DownloaderUtils.refreshFromServer(window);

            window.setVisible(true);
        }
        catch(Exception e)
        {
            DownloaderUtils.errorGUI("Could not retreive data from file", e, true);
        }
    }
}
