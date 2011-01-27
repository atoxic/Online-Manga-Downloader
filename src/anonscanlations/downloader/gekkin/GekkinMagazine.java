/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.gekkin;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class GekkinMagazine extends Magazine
{
    private TreeMap<String, Series> series;

    public GekkinMagazine()
    {
        series = new TreeMap<String, Series>();
    }

    public GekkinMagazine(Map<String, Object> yamlMap)
    {
        this();
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        return(ret);
    }

    public String getOriginalTitle()
    {
        return("Webコミック ゲッキン");
    }

    public Collection<Series> getSeries()
    {
        return(series.values());
    }
    
    public void addSeries(Series s)
    {
        series.put(s.getOriginalTitle(), s);
    }
}
