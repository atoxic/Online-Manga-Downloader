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
public class ComicHighSeries extends Series
{
    private String title;

    public ComicHighSeries(){}

    public ComicHighSeries(String myTitle)
    {
        title = myTitle;
    }

    public String getOriginalTitle()
    {
        return(title);
    }
}
