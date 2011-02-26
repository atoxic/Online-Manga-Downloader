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
import anonscanlations.downloader.comichigh.ComicHighSite;

/**
 *
 * @author /a/non
 */
public class PluginFreeChapter extends Chapter implements Serializable
{
    private Series series;
    private String title, url, downloadURL;
    private int total, sIS, nsn;

    public PluginFreeChapter(Series mySeries, Map<String, Object> yamlMap)
    {
        series = mySeries;
        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");
        downloadURL = (String)yamlMap.get("dlURL");
        total = (Integer)yamlMap.get("total");
        sIS = (Integer)yamlMap.get("sIS");
        nsn = (Integer)yamlMap.get("nsn");
    }

    public PluginFreeChapter(Series mySeries, String myTitle, String myURL, int myNSN)
    {
        series = mySeries;
        title = myTitle;
        url = myURL;
        
        downloadURL = null;
        total = 0;
        sIS = 0;
        nsn = myNSN;
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);
        ret.put("dlURL", downloadURL);
        ret.put("total", total);
        ret.put("sIS", sIS);
        ret.put("nsn", nsn);

        return(ret);
    }

    public boolean parsePages(String cgi) throws IOException
    {
        String indexPage = DownloaderUtils.getPage(url + "/index.shtml", "Shift_JIS");
        
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

    public Series getSeries()
    {
        return(series);
    }

    public String getTitle()
    {
        return(title);
    }

    public int getMin()
    {
        return(1);
    }
    public int getMax()
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

            dl.downloadProgressed(this, i);
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
