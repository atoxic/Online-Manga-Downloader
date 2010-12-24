/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.sunday;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class SundayMagazine extends Magazine
{
    private TreeMap<String, Series> series;
    public SundayMagazine()
    {
        series = new TreeMap<String, Series>();
    }

    public SundayMagazine(Map<String, Object> yamlMap)
    {
        this();
    }

    public Map<String, Object> dump()
    {
        HashMap<String, Object> ret = new HashMap<String, Object>();

        return(ret);
    }

    public void addSeries(Series s)
    {
        series.put(s.getOriginalTitle(), s);
    }

    public String getOriginalTitle() { return("クラブサンデー"); }
    public Collection<Series> getSeries()
    {
        return(series.values());
    }
}
