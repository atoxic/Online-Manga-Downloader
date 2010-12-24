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
public class GanGanOnlineMagazine extends Magazine
{
    private TreeMap<String, Series> series;

    public GanGanOnlineMagazine()
    {
        series = new TreeMap<String, Series>();
    }

    public GanGanOnlineMagazine(Map<String, Object> yamlMap)
    {
        this();
    }

    public void addSeries(Series s)
    {
        series.put(s.getOriginalTitle(), s);
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        
        return(ret);
    }

    public String getOriginalTitle()
    {
        return("ガンガンONLINE");
    }
    public Collection<Series> getSeries()
    {
        return(series.values());
    }
}
