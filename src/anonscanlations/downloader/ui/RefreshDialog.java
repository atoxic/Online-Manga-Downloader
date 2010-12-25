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
class RefreshDialog extends JDialog
{
    private DownloaderWindow window;
    private JPanel content;
    private TreeSet<String> selected;

    public RefreshDialog(DownloaderWindow myWindow)
    {
        super(myWindow, "Force Refresh List", true);
        window = myWindow;
        selected = new TreeSet<String>();

        WindowPrefsSaver.add("refreshDialog", this, false);

        content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setupUI();
        pack();
    }

    private void setupUI()
    {
        // Labels

        JPanel labels = new JPanel();
        labels.setLayout(new BoxLayout(labels, BoxLayout.PAGE_AXIS));
        labels.add(new JLabel("WARNING: refreshing takes several minutes!"), BorderLayout.NORTH);
        labels.add(new JLabel("Refresh the following:"), BorderLayout.NORTH);

        content.add(labels, BorderLayout.NORTH);

        // Check boxes

        JPanel checkBoxes = new JPanel();
        checkBoxes.setLayout(new BoxLayout(checkBoxes, BoxLayout.PAGE_AXIS));

        ItemListener itemListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent e)
            {
                String name = ((JCheckBox)e.getItem()).getText();
                if(e.getStateChange() == ItemEvent.SELECTED)
                    selected.add(name);
                else
                    selected.remove(name);
            }
        };

        for(String name : DownloadInfoServer.SITES.keySet())
        {
            JCheckBox checkBox = new JCheckBox(name);
            checkBox.addItemListener(itemListener);
            checkBoxes.add(checkBox);
        }

        content.add(checkBoxes, BorderLayout.CENTER);

        // Choices

        JPanel choice = new JPanel();

        JButton ok = new JButton("Refresh");
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                DownloaderUtils.debug("refresh: " + selected);
                RefreshDialog.this.dispose();

                final JDialog dialog = new JDialog(window, "Refreshing", true);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

                final JPanel dialogPanel = new JPanel();
                dialog.setContentPane(dialogPanel);

                final JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                dialogPanel.add(progressBar);

                dialog.pack();

                Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            refresh();
                            dialog.dispose();
                        }
                        catch(IOException ioe)
                        {
                            DownloaderUtils.errorGUI("Refresh error", ioe, false);
                        }
                    }
                };
                t.start();
                
                dialog.setVisible(true);
                
            }
        });
        choice.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                DownloaderUtils.debug("cancel");
                RefreshDialog.this.dispose();
            }
        });
        choice.add(cancel);

        content.add(choice, BorderLayout.SOUTH);
    }
    private void refresh() throws IOException
    {
        TreeMap<String, Magazine> magazines =
                new TreeMap<String, Magazine>();

        for(String name : selected)
        {
            magazines.putAll(DownloadInfoServer.SITES.get(name).getMagazines());
        }

        SaveData data = new SaveData();
        data.setMagazines(magazines);
        data.resetDate();

        window.addSaveData(data);
    }
}
