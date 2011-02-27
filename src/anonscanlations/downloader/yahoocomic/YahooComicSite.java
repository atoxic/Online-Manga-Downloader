/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.yahoocomic;

import anonscanlations.downloader.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public class YahooComicSite extends Site
{
    public final static YahooComicSite SITE = new YahooComicSite();

    private YahooComicSite()
    {
    }

    public static final String ROOT = "http://comics.yahoo.co.jp/magazine/index.html";

    public String getName(){ return("Yahoo Comic"); }

    public ArrayList<Magazine> getMagazines() throws IOException
    {
        ArrayList<Magazine> magazines = new ArrayList<Magazine>();

        String page = DownloaderUtils.getPage(ROOT, "EUC-JP");

        ArrayList<String> urls = urlInstances(page);

        for(String url : urls)
        {
            YahooComicMagazine mag = new YahooComicMagazine();
            mag.parsePage(url);

            magazines.add(mag);
        }

        return(magazines);
    }

    private static ArrayList<String> urlInstances(String page) throws MalformedURLException
    {
        ArrayList<String> urls = new ArrayList<String>();

        int index = 0;
        while((index = page.indexOf("全作品を見る", index + 1)) != -1)
        {
            String string = (new URL(new URL(ROOT), page.substring(page.lastIndexOf("<a href=\"", index) + 9, index - 2))).toString();
            urls.add(string);
        }

        return(urls);
    }
}
