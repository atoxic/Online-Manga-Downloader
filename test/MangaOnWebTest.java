import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.mangaonweb.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebTest
{
    @Test
    public void xmlTest() throws Exception
    {
        MangaOnWebChapter c = new MangaOnWebChapter("31110", "test");
        c.parseXML();
    }

    @Test
    public void scraperTest() throws Exception
    {
        Collection<Magazine> mags = MangaOnWebSite.SITE.getMagazines();

        for(Series s : mags.iterator().next().getSeries())
        {
            System.out.println("series: " + s);
            for(Chapter c : s.getChapters())
            {
                System.out.println("\tchapter: " + c);
            }
        }
    }
}
