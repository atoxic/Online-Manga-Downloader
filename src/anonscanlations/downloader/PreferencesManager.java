/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;

/**
 * 
 * @author /a/non
 */
public class PreferencesManager
{
    public static final Preferences PREFS = Preferences.userRoot().node("anonscanlations.downloader");

    public static final String KEY_SERVERCHECK = "serverCheck", 
                                KEY_SUBMIT = "submit",
                                KEY_DOWNLOADDIR = "downloadDir";

    private static final HashMap<Window, String> KEYS = new HashMap<Window, String>();

    private static final WindowListener WINDOW_LISTENER = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent e)
        {
            if(KEYS.containsKey(e.getWindow()))
            {
                DownloaderUtils.debug("PrefManager: Closing: " + KEYS.get(e.getWindow()));
                saveWindowPrefs(KEYS.get(e.getWindow()), e.getWindow());
            }
        }
        @Override
        public void windowClosed(WindowEvent e)
        {
            if(KEYS.containsKey(e.getWindow()))
            {
                DownloaderUtils.debug("PrefManager: Closed: " + KEYS.get(e.getWindow()));
                saveWindowPrefs(KEYS.get(e.getWindow()), e.getWindow());
            }
        }
    };
    private static final ComponentListener COMPONENT_LISTENER = new ComponentAdapter()
    {
        @Override
        public void componentHidden(ComponentEvent e)
        {
            Component c = e.getComponent();
            if(c instanceof Window && KEYS.containsKey((Window)c))
            {
                DownloaderUtils.debug("PrefManager: Hidden: " + KEYS.get((Window)c));
                saveWindowPrefs(KEYS.get((Window)c), (Window)c);
            }
        }
    };

    // set preferences to default values if they haven't been touched at all
    public static void initializePrefs()
    {
        if(!PREFS.getBoolean("initialized", false))
        {
            PREFS.putBoolean("initialized", true);
            PREFS.putBoolean(KEY_SERVERCHECK, true);
            PREFS.putBoolean(KEY_SUBMIT, false);
            PREFS.put(KEY_DOWNLOADDIR, new java.io.File(".").getAbsoluteFile().getParent());
        }
    }

    public static void registerWindow(String key, Window w, boolean size)
    {
        loadWindowPrefs(key, w, size);
        KEYS.put(w, key);
        w.addWindowListener(WINDOW_LISTENER);
        w.addComponentListener(COMPONENT_LISTENER);
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
        try
        {
            if(PREFS.nodeExists(key))
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
        catch(BackingStoreException bse)
        {
            DownloaderUtils.error("Couldn't load preferences for window: " + key, bse, false);
        }
    }
}
