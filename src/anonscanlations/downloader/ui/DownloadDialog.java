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
public class DownloadDialog extends JDialog
{
    private JPanel content;
    private JProgressBar progressBar;

    public DownloadDialog(DownloaderWindow window)
    {
        super(window, "Download Progress", true);

        setupUI();
    }

    private class DownloadDialogListener extends DownloadListener
    {
        int page;
        public DownloadDialogListener(DownloadDirectory myDir)
        {
            super(myDir);
            page = 0;
        }

        public void downloadIncrement(Chapter c)
        {
            page++;
            DownloaderUtils.debug("download progess: " + page);
            progressBar.setString("Page " + progressBar.getValue() + " out of " + (progressBar.getMaximum() - progressBar.getMinimum()));
            if(!progressBar.isIndeterminate())
                progressBar.setValue(page);
        }
        public void downloadFinished(Chapter c)
        {
            DownloaderUtils.debug("download finished!");
            dispose();
        }
        public void setTotal(int total)
        {
            DownloaderUtils.debug("setting total: " + total);
            progressBar.setMaximum(total);
            progressBar.setMinimum(0);
            progressBar.setIndeterminate(false);
        }
    }

    private void setupUI()
    {
        WindowPrefsSaver.add("progressDialog", this, false);

        content = new JPanel();
        setContentPane(content);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setStringPainted(true);
        
        content.add(progressBar);
        pack();
    }

    public void download(Chapter c)
    {
        final Chapter chapter = c;

        final DownloadDirectory dir = DownloadDirectory.makeDirectory(
                                        PreferencesManager.PREFS.get(
                                            PreferencesManager.KEY_DOWNLOADDIR,
                                            "./downloads/"));

        if(dir == null)
        {
            DownloaderUtils.errorGUI("Download error: couldn't create directory", null, false);
            return;
        }

        final DownloadListener dl = new DownloadDialogListener(dir);

        progressBar.setValue(0);
        progressBar.setMinimum(1);
        if(chapter.getTotal() != -1)
            progressBar.setMaximum(chapter.getTotal());
        else
            progressBar.setIndeterminate(true);

        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    chapter.download(dl);
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Download error", e, false);
                }
                DownloadDialog.this.dispose();
            }
        };
        t.start();
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                DownloaderUtils.debug("attempting to stop download");
                dl.abortDownload();
            }
        });
        setVisible(true);
    }
}
