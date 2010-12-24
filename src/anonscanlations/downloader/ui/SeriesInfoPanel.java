/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui;

import anonscanlations.downloader.*;
import anonscanlations.downloader.ui.field.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

/**
 *
 * @author /a/non
 */
public class SeriesInfoPanel extends JPanel
{
    private static final ArrayList<DisplayField> FIELDS = new ArrayList<DisplayField>();
    private static final HashMap<String, DisplayField> FIELDS_MAP = new HashMap<String, DisplayField>();
    private static void addDisplayField(DisplayField field)
    {
        FIELDS.add(field);
        FIELDS_MAP.put(field.getKey(), field);
    }
    static
    {
        addDisplayField(new NormalDisplayField("name", "Japanese Name", false));
        addDisplayField(new NormalDisplayField("translation", "Translated Name", true));
        addDisplayField(new MUDisplayField("mangaupdates", "MangaUpdates", true));
        addDisplayField(new ReComicDisplayField("recomic", "ReComic", true));
    }

    private DownloaderWindow window;
    private ImagePanel image;
    private JPanel infoPanel, fieldPanel, buttonPanel;
    private JButton submitButton, downloadButton;
    
    private HashMap<String, JTextField> fieldMap;
    private Chapter chapter;

    public SeriesInfoPanel(DownloaderWindow myWindow)
    {
        window = myWindow;
        fieldMap = new HashMap<String, JTextField>();
        chapter = null;

        setupUI();
    }
    private void setupUI()
    {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(400, 0));

        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        
        image = new ImagePanel();
        infoPanel.add(image);

        fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.PAGE_AXIS));
        infoPanel.add(fieldPanel);

        add(infoPanel);
        
        setupButtons();
        add(buttonPanel);
    }
    public void clear()
    {
        fieldPanel.removeAll();
        fieldPanel.updateUI();
        image.clear();
        setButtonStates(false, false);
        chapter = null;
        fieldMap.clear();
    }

    public void displaySeriesInfo(Series series, Chapter c)
    {
        HashMap seriesInfo = series.getSeriesInfo();
        
        // get information
        if(seriesInfo == null)
        {
            seriesInfo = new HashMap();
            seriesInfo.put("name", series.getOriginalTitle());
        }

        // clear the info panel
        clear();
        chapter = c;

        for(DisplayField field : FIELDS)
        {
            field.addLabel(fieldPanel, seriesInfo.get(field.getKey()));
            fieldMap.put(field.getKey(),
                    field.addField(fieldPanel, seriesInfo.get(field.getKey())));
        }
        setButtonStates(true, chapter != null);
    }

    private void setupButtons()
    {
        buttonPanel = new JPanel();

        submitButton = new JButton("Submit Information");
        submitButton.addActionListener(new ActionListener()
        {
            private String getSubmitURL(String ... keys) throws UnsupportedEncodingException
            {
                String ret = "http://anonscanlations.byethost14.com/submit.php?";
                
                if(keys.length == 0)
                    return(null);

                for(String key : keys)
                {
                    ret += key + "=";
                    if(fieldMap.containsKey(key))
                    {
                        JTextField field = fieldMap.get(key);
                        ret += URLEncoder.encode(field.getText(), "UTF-8");
                    }
                    ret += '&';
                }
                return(ret.substring(0, ret.length() - 1));
            }
            public void actionPerformed(ActionEvent ae)
            {
                String name = fieldMap.get("name").getText();
                HashMap info = DownloadInfoServer.SERIES_INFO.get(name);
                if(info == null)
                {
                    info = new HashMap();
                    DownloadInfoServer.SERIES_INFO.put(name, info);
                }
                for(Map.Entry<String, JTextField> fieldEntry : fieldMap.entrySet())
                {
                    info.put(fieldEntry.getKey(), FIELDS_MAP.get(fieldEntry.getKey()).getValue(fieldEntry.getValue()));
                }
                window.reorderSeriesNode(name);

                if(DownloadInfoServer.PREFS.getBoolean("submit", false))
                {
                    try
                    {
                        String submitURL = getSubmitURL("name",
                                                    "translation",
                                                    "mangaupdates",
                                                    "recomic");

                        if(submitURL == null)
                            return;

                        DownloaderUtils.getPage(submitURL, "ISO-8859-1");
                    }
                    catch(IOException ioe)
                    {
                        // TODO error on submission
                        DownloaderUtils.error("couldn't submit");
                    }
                }
            }
        });
        buttonPanel.add(submitButton);

        downloadButton = new JButton("Download");
        downloadButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                if(chapter != null)
                    window.downloader.download(chapter);
            }
        });
        buttonPanel.add(downloadButton);

        setButtonStates(false, false);

        buttonPanel.setMaximumSize(new Dimension(10000000, buttonPanel.getPreferredSize().height));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        add(buttonPanel);
    }

    public void setButtonStates(boolean maySave, boolean mayDownload)
    {
        submitButton.setText(DownloadInfoServer.PREFS.getBoolean("submit", false)
                                ? "Save and Submit" : "Save");
        submitButton.setEnabled(maySave);
        downloadButton.setEnabled(mayDownload);
    }
}
