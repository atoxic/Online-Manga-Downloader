import org.junit.*;
import static org.junit.Assert.*;

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
        MangaOnWebChapter c = new MangaOnWebChapter("31110");
        c.parseXML();
    }
}
