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
public class GekkinSeries extends Series
{
    private String title;

    public GekkinSeries()
    {
    }

    public GekkinSeries(String myTitle)
    {
        title = myTitle;
    }

    public String getOriginalTitle()
    {
        return(title);
    }
}
