/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non
 */
public abstract class Magazine extends YAMLable implements Serializable
{
    private ArrayList<Series> series;
    public Magazine()
    {
        series = new ArrayList<Series>();
    }

    public abstract String getOriginalTitle();

    public final Collection<Series> getSeries()
    {
        return(series);
    }
    public final void addSeries(Series s)
    {
        s.setMagazine(this);
        series.add(s);
    }

    public final HashMap getMagazineInfo()
    {
        return((HashMap)DownloadInfoServer.MAGAZINE_INFO.get(getOriginalTitle()));
    }

    public final String getTranslatedTitle()
    {
        HashMap info = getMagazineInfo();
        if(info != null)
        {
            if(info.containsKey("translation"))
                return((String)info.get("translation"));
        }
        return(getOriginalTitle());
    }

    @Override
    public final String toString()
    {
        return(getTranslatedTitle());
    }
}
