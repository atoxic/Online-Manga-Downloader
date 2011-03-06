import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;

import anonscanlations.downloader.crochettime.*;

/**
 *
 * @author /a/non
 */
public class CrochetTimeTest
{
    public CrochetTimeTest()
    {
    }

    @Test
    public void scrambleTest()
    {
        String scrambled = CrochetTimeChapter.scrambleURL("/home/dotbook/rs2_contents/voyager-store_contents/amwdc0002/amwdc0002_pc_image_crochet.book.bmit&B00006032");

        assertEquals(scrambled,
                "7tczwnjvwwmxj%2Fj7p_es%26_lxf%2Ftidtkuyzafpe8e-xc_.xczpxgha7b3%2F73%2Faireecj_wktkjj%2FG%2F.zyzrsf7qu239f_2t0fj1zd48uuy4");
    }

    @Test
    public void fileListTest() throws IOException
    {
        // TODO: check results
        CrochetTimeChapter.fileList("testfiles/dBmd_amwdc0002");
    }
}
