/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.awt.*;
import java.util.*;
import java.util.prefs.*;

/**
 * 
 * @author /a/non
 */
public class PreferencesManager
{
    public static final Preferences PREFS = Preferences.userRoot().node("anonscanlations.downloader");

    public static final String KEY_SERVERCHECK = "serverCheck", KEY_SUBMIT = "submit";

    // set preferences to default values if they haven't been touched at all
    public static void initializePrefs()
    {
        if(!PREFS.getBoolean("initialized", false))
        {
            PREFS.putBoolean("initialized", true);
            PREFS.putBoolean(KEY_SERVERCHECK, true);
            PREFS.putBoolean(KEY_SUBMIT, false);
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
