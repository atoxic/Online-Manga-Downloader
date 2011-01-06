/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.comichigh;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class ComicHighMagazine extends Magazine
{
    private TreeMap<String, Series> series;

    public ComicHighMagazine()
    {
        series = new TreeMap<String, Series>();
    }

    public ComicHighMagazine(Map<String, Object> yamlMap)
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
        return("コミックハイ！");
    }

    public Collection<Series> getSeries()
    {
        return(null);
    }
    
    public void addSeries(Series s)
    {
        
    }
}
