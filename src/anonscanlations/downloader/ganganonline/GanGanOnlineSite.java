/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ganganonline;

import anonscanlations.downloader.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public class GanGanOnlineSite extends Site
{
    public final static GanGanOnlineSite SITE = new GanGanOnlineSite();

    private GanGanOnlineSite()
    {
    }

    public static final String ROOT = "http://www.square-enix.com/jp/magazine/ganganonline/comic/";

    public String getName(){ return("GanGan Online"); }

    public ArrayList<Magazine> getMagazines() throws IOException
    {
        ArrayList<Magazine> magazines =
                new ArrayList<Magazine>();
        
        String page = DownloaderUtils.getPage(ROOT, "Shift_JIS");

        TreeSet<String> list = titleInstances(page);
        
        GanGanOnlineMagazine ggOnline = new GanGanOnlineMagazine();
        magazines.add(ggOnline);
        
        for(String titlePanels : list)
        {
            GanGanOnlineSeries s = new GanGanOnlineSeries(titlePanels);
            s.parsePage();
            ggOnline.addSeries(s);
        }

        return(magazines);
    }
    
    private static TreeSet<String> titleInstances(String page)
    {
        TreeSet<String> list = new TreeSet<String>();

        int index = 0;
        while((index = page.indexOf("<div class=\"titlePanels\">", index + 1)) != -1)
        {
            String string = page.substring(index, page.indexOf("</div>", index));
            list.add(string);
        }

        return(list);
    }
}
