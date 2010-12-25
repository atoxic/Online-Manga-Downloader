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
public class DownloaderWindow extends JFrame
{
    // package scope
    SeriesInfoPanel info;
    JTree tree;
    DownloadDialog downloader;

    private SaveData data;

    public DownloaderWindow(SaveData myData)
    {
        super("Online Manga Downloader");

        data = myData;
        downloader = new DownloadDialog(this);

        setupUI();

        WindowPrefsSaver.add("mainWindow", this, true);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                DownloaderUtils.debug("attempting to save data");

                try
                {
                    data.dumpYAML("data/manga_download_info.yml");
                    DownloadInfoServer.saveAllInfo();

                    DownloaderUtils.debug("data saved");
                }
                catch(IOException ioe)
                {
                    DownloaderUtils.error("Couldn't save data to file", ioe, false);
                }

                System.exit(0);
            }
        });
    }

    public void setTreeState(boolean enable){ tree.setEnabled(enable); }

    private void setupUI()
    {
        JPanel content = new JPanel(new BorderLayout());
        setContentPane(content);

        // Info panel in the side

        info = new SeriesInfoPanel(this);
        content.add(info, BorderLayout.EAST);

        // Tree in center

        tree = new JTree();
        tree.addMouseListener(new DownloaderWindowAdapter(this));
        tree.setMinimumSize(new Dimension(0, 800));

        rebuildTree();

        JScrollPane scroll = new JScrollPane(tree);
        content.add(scroll, BorderLayout.CENTER);

        // Menu

        setupMenu();
    }

    public void setupMenu()
    {
        JMenuBar menuBar = new JMenuBar();

        {
            JMenu file = new JMenu("File");
            file.setMnemonic(KeyEvent.VK_F);
            menuBar.add(file);

            {
                JMenuItem manuallyRefresh = new JMenuItem("Manually Refresh");
                manuallyRefresh.setMnemonic(KeyEvent.VK_M);
                manuallyRefresh.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_M, ActionEvent.CTRL_MASK));
                manuallyRefresh.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        RefreshDialog dialog = new RefreshDialog(DownloaderWindow.this);
                        dialog.setVisible(true);
                    }
                });
                file.add(manuallyRefresh);
            }

            {
                JMenuItem refresh = new JMenuItem("Refresh from Server");
                refresh.setMnemonic(KeyEvent.VK_R);
                refresh.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_R, ActionEvent.CTRL_MASK));
                refresh.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        DownloaderUtils.refreshFromServer(DownloaderWindow.this);
                    }
                });
                file.add(refresh);
            }

            file.addSeparator();

            {
                JMenuItem preferences = new JMenuItem("Preferences");
                preferences.setMnemonic(KeyEvent.VK_P);
                preferences.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_P, ActionEvent.CTRL_MASK));
                preferences.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        PreferencesDialog dialog = new PreferencesDialog(DownloaderWindow.this);
                        dialog.setVisible(true);
                    }
                });
                file.add(preferences);
            }

            file.addSeparator();

            {
                JMenuItem exit = new JMenuItem("Exit");
                exit.setMnemonic(KeyEvent.VK_X);
                exit.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        System.exit(0);
                    }
                });
                file.add(exit);
            }
        }
        {
            JMenu help = new JMenu("Help");
            help.setMnemonic(KeyEvent.VK_H);
            menuBar.add(help);

            {
                JMenuItem about = new JMenuItem("About");
                about.setMnemonic(KeyEvent.VK_A);
                about.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent ae)
                    {
                        JOptionPane.showMessageDialog(null, 
                                DownloaderUtils.makeHyperlinkLabel(Downloader.ABOUT),
                                "About",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                help.add(about);
            }
        }

        setJMenuBar(menuBar);
    }

    public void rebuildTree()
    {
        SortedTreeNode top = new SortedTreeNode("Magazines");

        for(Map.Entry<String, Magazine> mag : data.getMagazines().entrySet())
        {
            SortedTreeNode magazineNode = new SortedTreeNode(mag.getValue().getTranslatedTitle());
            for(Series series : mag.getValue().getSeries())
            {
                SortedTreeNode seriesNode = new SortedTreeNode(series);
                for(Chapter chapter : series.getChapters())
                {
                    SortedTreeNode chapterNode = new SortedTreeNode(chapter);
                    seriesNode.add(chapterNode);
                }
                magazineNode.add(seriesNode);
            }
            top.add(magazineNode);
        }

        DefaultTreeModel model = new DefaultTreeModel(top);

        tree.setModel(model);
    }

    public void addSaveData(SaveData data)
    {
        SaveData newer, older;
        if(data.getDate().after(this.data.getDate()))
        {
            newer = data;
            older = this.data;
        }
        else
        {
            newer = this.data;
            older = data;
        }

        older.getMagazines().putAll(newer.getMagazines());

        this.data = older;

        rebuildTree();
    }
}

