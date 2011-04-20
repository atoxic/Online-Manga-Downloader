/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jdesktop.swingx.*;
import org.jdesktop.swingx.treetable.*;

import anonscanlations.downloader.actibook.*;

/**
 *
 * @author Administrator
 */
public class Downloader
{
    public static void main(String[] args)
    {
        final JFrame frame = new JFrame("Online Manga Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        PreferencesManager.registerWindow("omd0.1.x_mainWindow", frame, true);
        frame.setContentPane(makeContentPane());
        frame.setVisible(true);
    }
    private static JPanel makeContentPane()
    {
        final JPanel content = new JPanel(new BorderLayout());

        // ========================
        // TREE
        // ========================
        final DefaultTreeTableModel treeTableModel = new DefaultTreeTableModel();
        final DefaultMutableTreeTableNode root = new DefaultMutableTreeTableNode();
        treeTableModel.setRoot(root);
        final JXTreeTable treeTable = new JXTreeTable(treeTableModel);
        final JScrollPane scrollpane = new JScrollPane(treeTable);
        content.add(scrollpane, "Center");

        // ========================
        // TREEMODEL
        // ========================

        final ArrayList<String> columns = new ArrayList<String>();
        columns.add("Name");
        columns.add("Progress");
        columns.add("Status");
        columns.add("Size");
        treeTableModel.setColumnIdentifiers(columns);

        // ========================
        // BUTTON
        // ========================

        final JPanel buttons = new JPanel();
        final JButton add = new JButton("Add");
        buttons.add(add);

        add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                final DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode();
                final DownloadListener listener = new DownloadListener(DownloadDirectory.makeDirectory(
                                PreferencesManager.PREFS.get(PreferencesManager.KEY_DOWNLOADDIR, "./downloads/")))
                {
                    public void downloadIncrement(Chapter c)
                    {
                        DownloaderUtils.debug("downloadProgressed: chapter " + c);
                    }
                    public void downloadFinished(Chapter c)
                    {
                        treeTableModel.removeNodeFromParent(node);
                        DownloaderUtils.debug("downloadFinished: chapter " + c);
                    }
                    public void setTotal(int total)
                    {
                        DownloaderUtils.debug("setTotal:"  + total);
                    }
                };

                treeTableModel.insertNodeInto(node, root, 0);

                ActibookChapter chapter = new ActibookChapter("Ryuushika Ryuushika c01",
                        "http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/");
                try
                {
                    chapter.init();
                    chapter.download(listener);
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Error in download", e, false);
                }
            }
        });
        content.add(buttons, "South");

        return(content);
    }
}
