/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui;

import anonscanlations.downloader.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.imageio.*;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author /a/non
 */
public class WindowPrefsSaver extends WindowAdapter
{
    private String key;
    private boolean size;
    private WindowPrefsSaver(String myKey, Window w, boolean size)
    {
        key = myKey;
        this.size = size;
        PreferencesManager.loadWindowPrefs(key, w, size);
        w.addWindowListener(this);
    }

    public static void add(String myKey, Window w, boolean size)
    {
        WindowPrefsSaver saver = new WindowPrefsSaver(myKey, w, size);
    }

    @Override
    public void windowOpened(WindowEvent e)
    {
        DownloaderUtils.debug("loading prefs for: " + key);
        PreferencesManager.loadWindowPrefs(key, e.getWindow(), size);
    }

    @Override
    public void windowClosing(WindowEvent e)
    {
        DownloaderUtils.debug("closing; saving prefs for: " + key);
        PreferencesManager.saveWindowPrefs(key, e.getWindow());
    }

    @Override
    public void windowClosed(WindowEvent e)
    {
        DownloaderUtils.debug("closed; saving prefs for: " + key);
        PreferencesManager.saveWindowPrefs(key, e.getWindow());
    }
}
