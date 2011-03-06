import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

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
        ArrayList<String> test = CrochetTimeChapter.fileList(new File("testfiles/dBmd_amwdc0002"));

        RandomAccessFile results = new RandomAccessFile("testfiles/dBmd_amwdc0002_res", "r");

        for(String t : test)
        {
            String line = results.readLine();
            assertTrue(t.startsWith(line));
        }
    }

    @Test
    public void decryptFileTest() throws IOException
    {
        CrochetTimeChapter.decryptFile(new File("testfiles_gitignore/dengeki2_first"),
                                    new File("testfiles_gitignore/out"));

        RandomAccessFile test = new RandomAccessFile("testfiles_gitignore/out", "r"),
                        res = new RandomAccessFile("testfiles_gitignore/dengeki2_first_res", "r");
        assertEquals(test.length(), res.length());
        int in, out;
        while((in = test.read()) != -1 && (out = res.read()) != -1)
        {
            assertEquals(in, out);
        }
    }

    @Test
    public void downloadTest() throws Exception
    {
        CrochetTimeChapter c = new CrochetTimeChapter();
        DummyDownloadListener dl = new DummyDownloadListener();

        assertTrue(c.download(dl));
    }
}
