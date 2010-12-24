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
class DownloaderWindowAdapter extends MouseAdapter
{
    private DownloaderWindow window;

    public DownloaderWindowAdapter(DownloaderWindow myWindow)
    {
        window = myWindow;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int selRow = window.tree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = window.tree.getPathForLocation(e.getX(), e.getY());
        if(e.getClickCount() == 1)
        {
            if(selRow != -1)
            {
                if(selPath.getPathCount() >= 3)
                {
                    Series series = (Series) ((DefaultMutableTreeNode)selPath.getPathComponent(2)).getUserObject();
                    Chapter c = null;
                    if(selPath.getPathCount() == 4)
                        c = (Chapter) ((DefaultMutableTreeNode)selPath.getPathComponent(3)).getUserObject();
                    window.info.displaySeriesInfo(series, c);
                }
                else
                {
                    window.info.clear();
                }
            }
        }
        else if(e.getClickCount() == 2)
        {
            if(selRow != -1 && selPath.getPathCount() == 4)
            {
                final Chapter chapter = (Chapter) ((DefaultMutableTreeNode)selPath.getLastPathComponent()).getUserObject();

                window.downloader.download(chapter);
            }
        }
    }
}