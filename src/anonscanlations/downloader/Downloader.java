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
import anonscanlations.downloader.sunday.*;

/**
 *
 * @author /a/non
 */
public class Downloader
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            DownloadInfoServer.loadAllInfo();
        }
        catch(Exception e)
        {
            DownloaderUtils.error("Couldn't load info on series or magazines");
        }

        DownloadInfoServer.SITES.put(SundaySite.SITE.getName(), SundaySite.SITE);
        DownloadInfoServer.SITES.put(GanGanOnlineSite.SITE.getName(), GanGanOnlineSite.SITE);
        DownloadInfoServer.SITES.put(YahooComicSite.SITE.getName(), YahooComicSite.SITE);

        DownloadInfoServer.initializePrefs();

        if(args.length >= 1 && args[0].equals("--server"))
        {
            server(args.length >= 2 ? args[1] :
                                DownloadInfoServer.SAVE_LOCATION);
        }
        else
        {
            //sundayDemo();
            client();
        }
    }

    private static void server(String file)
    {
        DownloaderUtils.debug("running server");

        TreeMap<String, Magazine> magazines =
                new TreeMap<String, Magazine>();

        for(Map.Entry<String, Site> entry : DownloadInfoServer.SITES.entrySet())
        {
            TreeMap<String, Magazine> siteMagazines = null;
            try
            {
                siteMagazines = entry.getValue().getMagazines();
            }
            catch(IOException ioe)
            {
                DownloaderUtils.error("Could not get data on magazine \""
                                    + entry.getKey() + "\"");
                System.exit(1);
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
            DownloaderUtils.error("Could not save data");
            System.exit(1);
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

            if(DownloadInfoServer.PREFS.getBoolean("serverCheck", true))
                retreiveFromServer(window);

            window.setVisible(true);
        }
        catch(Exception e)
        {
            DownloaderUtils.error("Could not retreive data from file");
        }
    }

    private static void retreiveFromServer(DownloaderWindow w)
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
                    URL object = new URL("https://dl.dropbox.com/u/6792608/manga_download_info.yml");

                    SaveData data = DownloaderUtils.readYAML(object.openStream());

                    window.addSaveData(data);
                }
                catch(Exception e)
                {
                    // TODO: display status on error
                    DownloaderUtils.error("couldn't retreive data from server");
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
