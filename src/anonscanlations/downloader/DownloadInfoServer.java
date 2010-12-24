/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.prefs.*;

import org.yaml.snakeyaml.*;

/**
 * A bunch of utility functions and static stuff
 * This file used to be the server part of the program before
 * I merged the client and the server.
 * That's why it's called DownloadInfoServer (for now)
 * @author /a/non
 */
public class DownloadInfoServer
{
    public static final Preferences PREFS = Preferences.userRoot().node("anonscanlations.downloader");

    //public static final String SAVE_LOCATION = "E:/dropbox/My Dropbox/Public/manga_download_info.yml";
    public static final String SAVE_LOCATION = "C:/Users/Administrator/Documents/"
                                        + "My Dropbox/Public/manga_download_info.yml";
    
    public static final Map<String, HashMap> SERIES_INFO = Collections.synchronizedMap(new TreeMap<String, HashMap>()),
                                                MAGAZINE_INFO = Collections.synchronizedMap(new TreeMap<String, HashMap>());

    public static final TreeMap<String, Site> SITES = new TreeMap<String, Site>();

    public static void loadInfo(String file, Map<String, HashMap> infoMap) throws IOException
    {
        Yaml yaml = new Yaml();

        FileInputStream input = new FileInputStream(new File(file));

        for(Object data : yaml.loadAll(input))
        {
            HashMap map = (HashMap)data;
            infoMap.put((String)map.get("name"), map);
        }

        input.close();
    }

    public static void loadAllInfo() throws IOException
    {
        loadInfo("data/series.yml", SERIES_INFO);
        loadInfo("data/magazines.yml", MAGAZINE_INFO);
    }

    // set preferences to default values if they haven't been touched at all
    public static void initializePrefs()
    {
        if(!PREFS.getBoolean("initialized", false))
        {
            PREFS.putBoolean("initialized", true);
            PREFS.putBoolean("serverCheck", true);
            PREFS.putBoolean("submit", false);
        }
    }

    public static void saveWindowPrefs(String key, Window w)
    {
        Preferences node = PREFS.node(key);
        Rectangle bounds = w.getBounds();
        node.putInt("x", bounds.x);
        node.putInt("y", bounds.y);
        node.putInt("width", bounds.width);
        node.putInt("height", bounds.height);
    }

    public static void loadWindowPrefs(String key, Window w, boolean size)
    {
        Preferences node = PREFS.node(key);
        Rectangle bounds = w.getBounds();
        bounds.x = node.getInt("x", bounds.x);
        bounds.y = node.getInt("y", bounds.y);
        if(size)
        {
            bounds.width = node.getInt("width", bounds.width);
            bounds.height = node.getInt("height", bounds.height);
        }
        w.setBounds(bounds);
    }
}
