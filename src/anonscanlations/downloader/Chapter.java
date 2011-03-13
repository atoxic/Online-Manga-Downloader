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
public abstract class Chapter extends YAMLable implements Serializable
{
    private Series series;

    public final void setSeries(Series mySeries)
    {
        series = mySeries;
    }
    public final Series getSeries()
    {
        return(series);
    }
    public abstract String getTitle();
    public abstract int getTotal();

    @Override
    public String toString()
    {
        return(getTitle());
    }

    public abstract boolean download(DownloadListener dl) throws Exception;
}
