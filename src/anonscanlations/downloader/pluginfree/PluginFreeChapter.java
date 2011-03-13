/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.pluginfree;

import java.util.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class PluginFreeChapter extends Chapter implements Serializable
{
    private String title, url, downloadURL;
    private int total, sIS, nsn;

    public PluginFreeChapter(){}

    public PluginFreeChapter(String myTitle, String myURL, int myNSN)
    {
        title = myTitle;
        url = myURL;
        
        downloadURL = null;
        total = 0;
        sIS = 0;
        nsn = myNSN;
    }

    public boolean parsePages(String cgi) throws IOException
    {
        String indexPage = DownloaderUtils.getPage(url + "/index.shtml", "Shift_JIS");
        if(indexPage == null)
            return(false);
        
        // content Key Value
        String cKV = title(indexPage, "'cKV'");
        if(cKV == null)
            return(false);
        DownloaderUtils.debug("\t\t\tcKV: " + cKV);
        int cKVInt = Integer.parseInt(cKV);

        // root directory
        String hCN = title(indexPage, "'hCN'");
        if(hCN == null)
            return(false);
        DownloaderUtils.debug("\t\t\thCN: " + hCN);

        String initValEncoded = DownloaderUtils.getPage(url + "/InitVal.html", "Shift_JIS");

        int index = initValEncoded.indexOf("<DIV ID='DATA'>");
        if(index == -1)
            return(false);
        String initVal = PluginFreeDecrypt.expand(initValEncoded.substring(index + 15, initValEncoded.indexOf("</DIV>", index)), cKVInt);

        // page number
        String tPN = title(initVal, "'tPN'");
        if(tPN == null)
            return(false);
        total = Integer.parseInt(tPN);
        DownloaderUtils.debug("\t\t\ttotal: " + total);

        // for decoding page individual files
        String sISString = title(initVal, "'sIS'");
        if(tPN == null)
            return(false);
        sIS = Integer.parseInt(sISString);
        DownloaderUtils.debug("\t\t\tsIS: " + sIS);

        // title
        String sHN = title(initVal, "'sHN'");
        if(sHN == null)
            return(false);
        DownloaderUtils.debug("\t\t\tsHN: " + sHN);

        downloadURL = cgi + hCN + sHN + "/" + sHN;
        DownloaderUtils.debug("\t\t\ttarget: " + downloadURL);
        //DownloaderUtils.debug("\t\t\t" + ComicHighSite.getIpntStr(sIS, 1, 15, 0, 0));
        //DownloaderUtils.debug("\t\t\t" + ComicHighSite.getIpntStr(sIS, 1, 15, 1, 0));
        //DownloaderUtils.debug("\t\t\t" + ComicHighSite.getIpntStr(sIS, 1, 15, 1, 1));
        //DownloaderUtils.debug("\t\t\t" + ComicHighSite.getIpntStr(sIS, 1, 15, 0, 1));

        return(true);
    }

    public String getTitle()
    {
        return(title);
    }

    public int getTotal()
    {
        return(total);
    }

    public boolean download(DownloadListener dl) throws IOException
    {
        for(int i = 1; i <= total; i++)
        {
            if(dl.isDownloadAborted())
                return(true);

            String prefix = downloadURL + "_" + String.format("%03d", i) + "_" + nsn;
            
            BufferedImage topLeft   =   DownloaderUtils.downloadImage(new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 0, 0) + ".jpg"));
            BufferedImage topRight  =   DownloaderUtils.downloadImage(new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 1, 0) + ".jpg"));
            BufferedImage botLeft   =   DownloaderUtils.downloadImage(new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 0, 1) + ".jpg"));
            BufferedImage botRight  =   DownloaderUtils.downloadImage(new URL(prefix + PluginFreeDecrypt.getIpntStr(sIS, i, nsn, 1, 1) + ".jpg"));

            BufferedImage complete = new BufferedImage(topLeft.getWidth() + topRight.getWidth(),
                                                        topLeft.getHeight() + botLeft.getHeight(),
                                                        BufferedImage.TYPE_INT_RGB);

            Graphics2D g = complete.createGraphics();
            g.drawImage(topLeft,    0,                      0, null);
            g.drawImage(topRight,   topLeft.getWidth(),     0, null);
            g.drawImage(botLeft,    0,                      topLeft.getHeight(), null);
            g.drawImage(botRight,   topLeft.getWidth(),     topLeft.getHeight(), null);

            String path = dl.downloadPath(this, i);
            ImageIO.write(complete, "JPEG", new File(path));

            dl.downloadIncrement(this);
        }

        dl.downloadFinished(this);

        return(true);
    }

    private String title(String page, String ID)
    {
        int index = page.indexOf("ID=" + ID);
        if(index != -1)
        {
            index = page.indexOf("title='", index);
            if(index != -1)
                return(page.substring(index + 7, page.indexOf('\'', index + 7)));
        }
        return(null);
    }
}
