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
    SeriesInfoPanel info;
    JTree tree;
    DownloadDialog downloader;

    private SaveData data;
    private TreeMap<String, SortedTreeNode> seriesNodeMap;

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

                    DownloaderUtils.debug("data saved");
                }
                catch(IOException ioe)
                {
                    DownloaderUtils.error("Couldn't save data to file", false);
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

        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);

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

        file.addSeparator();

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

        setJMenuBar(menuBar);
    }

    public void rebuildTree()
    {
        SortedTreeNode top = new SortedTreeNode("Magazines");

        seriesNodeMap = new TreeMap<String, SortedTreeNode>();

        for(Map.Entry<String, Magazine> mag : data.getMagazines().entrySet())
        {
            SortedTreeNode magazineNode = new SortedTreeNode(mag.getValue().getTranslatedTitle());
            for(Series series : mag.getValue().getSeries())
            {
                SortedTreeNode seriesNode = new SortedTreeNode(series);
                seriesNodeMap.put(series.getOriginalTitle(), seriesNode);
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

    /** Force a node that may have changed to be put in its right place
     * 
     * @param name
     */
    public void reorderSeriesNode(String name)
    {
        SortedTreeNode seriesNode = seriesNodeMap.get(name);
        if(seriesNode != null)
        {
            ((SortedTreeNode)seriesNode.getParent()).add(seriesNode);
        }
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

