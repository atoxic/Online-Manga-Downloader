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

class MUImageLoadThread extends Thread
{
    private ImagePanel panel;
    private String url;

    public MUImageLoadThread(ImagePanel myPanel, String myURL)
    {
        panel = myPanel;
        url = myURL;
    }

    @Override
    public void run()
    {
        try
        {
            if(ImagePanel.IMAGES.containsKey(url))
            {
                panel.load(url, null);
            }
            else
            {
                String page = DownloaderUtils.getPage(url, "ISO-8859-1");
                int index = page.indexOf("http://www.mangaupdates.com/image/i");
                if(index != -1)
                {
                    String image = page.substring(index, page.indexOf(".jpg", index) + 4);
                    panel.load(url, image);
                }
            }
        }
        catch(IOException ioe)
        {
            DownloaderUtils.error("Couldn't load image", ioe, false);
        }
    }
}

class ImagePanel extends JPanel
{
    public static final TreeMap<String, String> URLS =
                        new TreeMap<String, String>();
    public static final TreeMap<String, BufferedImage> IMAGES =
                        new TreeMap<String, BufferedImage>();

    private String url;
    private BufferedImage image;
    private Image scaledImage;
    private int imageWidth, imageHeight;
    public ImagePanel()
    {
        super();

        clear();

        setMinimumSize(new Dimension(0, 350));
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    public synchronized void load(String key, String myURL) throws IOException
    {
        if(IMAGES.containsKey(key))
        {
            image = IMAGES.get(key);
            url = URLS.get(key);
        }
        else
        {
            url = myURL;
            image = ImageIO.read(new URL(myURL));
            IMAGES.put(key, image);
            URLS.put(key, url);
        }

        repaint();
    }

    public void clear()
    {
        url = null;
        image = null;
        scaledImage = null;
        imageWidth = imageHeight = 0;
    }

    public boolean isLoaded(){ return(image != null); }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(isLoaded())
        {
            // do we resize by width or height?
            boolean useHeight = image.getHeight() > image.getWidth();

            // check against the panel height
            if(useHeight)
                useHeight = image.getWidth() * getHeight() / image.getHeight() < getWidth();
            else
                useHeight = image.getHeight() * getWidth() / image.getWidth() > getHeight();

            if(useHeight)
            {
                imageWidth = image.getWidth() * getHeight() / image.getHeight();
                imageHeight = getHeight();
            }
            else
            {
                imageWidth = getWidth();
                imageHeight = image.getHeight() * getWidth() / image.getWidth();
            }

            scaledImage = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

            g.drawImage(scaledImage,
                    (getWidth() - imageWidth) / 2,
                    (getHeight() - imageHeight) / 2,
                    this);
        }
    }
}