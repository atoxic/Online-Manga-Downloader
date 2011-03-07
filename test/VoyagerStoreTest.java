import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.voyagerstore.*;

/**
 *
 * @author /a/non
 */
public class VoyagerStoreTest
{
    @Test
    public void scrapeTest() throws IOException
    {
        Collection<Magazine> mags = VoyagerStoreSite.SITE.getMagazines();
        for(Magazine m : mags)
        {
            for(Series s : m.getSeries())
            {
                DownloaderUtils.debug("series: " + s);
                for(Chapter c : s.getChapters())
                {
                    DownloaderUtils.debug("\tchapter: " + c);
                }
            }
        }
    }
}
