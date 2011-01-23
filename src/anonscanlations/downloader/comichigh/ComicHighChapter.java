/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.comichigh;

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
public class ComicHighChapter extends Chapter implements Serializable
{
    private Series series;
    private String title, url, downloadURL;
    private int total, sIS;

    public ComicHighChapter(Series mySeries, Map<String, Object> yamlMap)
    {
        series = mySeries;
        title = (String)yamlMap.get("title");
        url = (String)yamlMap.get("url");
        downloadURL = (String)yamlMap.get("dlURL");
        total = (Integer)yamlMap.get("total");
        sIS = (Integer)yamlMap.get("sIS");
    }

    public ComicHighChapter(Series mySeries, String myTitle, String myURL)
    {
        series = mySeries;
        title = myTitle;
        url = myURL;
        
        downloadURL = null;
        total = 0;
        sIS = 0;
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        ret.put("title", title);
        ret.put("url", url);
        ret.put("dlURL", downloadURL);
        ret.put("total", total);
        ret.put("sIS", sIS);

        return(ret);
    }

    public boolean parsePages() throws IOException
    {
        String indexPage = DownloaderUtils.getPage(url + "/index.shtml", "Shift_JIS");
        
        // content Key Value
        String cKV = title(indexPage, "'cKV'");
        DownloaderUtils.debug("\t\t\tcKV: " + cKV);
        int cKVInt = Integer.parseInt(cKV);

        // root directory
        String hCN = title(indexPage, "'hCN'");
        DownloaderUtils.debug("\t\t\thCN: " + hCN);

        String initValEncoded = DownloaderUtils.getPage(url + "/InitVal.html", "Shift_JIS");

        int index = initValEncoded.indexOf("<DIV ID='DATA'>");
        if(index == -1)
            return(false);
        String initVal = ComicHighSite.expand(initValEncoded.substring(index + 15, initValEncoded.indexOf("</DIV>", index)), cKVInt);

        // page number
        String tPN = title(initVal, "'tPN'");
        total = Integer.parseInt(tPN);
        DownloaderUtils.debug("\t\t\ttotal: " + total);

        // for decoding page individual files
        String sISString = title(initVal, "'sIS'");
        sIS = Integer.parseInt(sISString);
        DownloaderUtils.debug("\t\t\tsIS: " + sIS);

        // title
        String sHN = title(initVal, "'sHN'");
        DownloaderUtils.debug("\t\t\tsHN: " + sHN);

        downloadURL = "http://futabasha.pluginfree.com/cgi-bin/widget.cgi?a=" + hCN + sHN + "/" + sHN;
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

            String prefix = downloadURL + "_" + String.format("%03d", i) + "_15";
            BufferedImage topLeft   =   DownloaderUtils.downloadImage(new URL(prefix + ComicHighSite.getIpntStr(sIS, i, 15, 0, 0) + ".jpg"));
            BufferedImage topRight  =   DownloaderUtils.downloadImage(new URL(prefix + ComicHighSite.getIpntStr(sIS, i, 15, 1, 0) + ".jpg"));
            BufferedImage botLeft   =   DownloaderUtils.downloadImage(new URL(prefix + ComicHighSite.getIpntStr(sIS, i, 15, 0, 1) + ".jpg"));
            BufferedImage botRight  =   DownloaderUtils.downloadImage(new URL(prefix + ComicHighSite.getIpntStr(sIS, i, 15, 1, 1) + ".jpg"));

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
