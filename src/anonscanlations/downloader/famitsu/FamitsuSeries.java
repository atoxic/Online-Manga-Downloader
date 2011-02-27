/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.famitsu;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class FamitsuSeries extends Series
{
    private String title;

    public FamitsuSeries()
    {
    }

    public FamitsuSeries(String myTitle)
    {
        title = myTitle;
    }

    public String getOriginalTitle()
    {
        return(title);
    }
}
