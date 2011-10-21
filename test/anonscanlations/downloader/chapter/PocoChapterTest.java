/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import org.junit.*;
import static org.junit.Assert.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PocoChapterTest extends PocoChapter        // So I can access private members
{
    private static final long CRC32[] =
            new long[]{878575707L, 4258525291L, 2038831088L, 4276476124L,
                    2075900910L, 109744020L, 3661107503L, 2762017388L,
                    461546871L, 68445120L, 1897989602L, 1722973420L, 2670171597L};

    public PocoChapterTest(){}

    @Before
    public void setUp() throws Exception
    {
        Downloader.init();
    }

    /**
     * Test of init method, of class PocoChapter.
     */
    @Test
    public void testInit() throws Exception
    {
        PocoChapter instance = new PocoChapter(new URL("http://www.poco2.jp/viewer/play.php?partid=735b90b4568125ed6c3f678819b6e058"));
        downloader().pause();
        instance.init();
        downloader().pause();
        downloader().waitUntilFinished();

        assertTrue(instance.images.get(0).equals("http://www.poco2.jp/viewerset/story/0016/0048/00000067/650.jpg"));
    }

    /**
     * Test of download method, of class PocoChapter.
     */
    @Test
    public void testDownload() throws Exception
    {
        File directory = TestUtils.createTempDirectory();
        PocoChapter instance = new PocoChapter(new URL("http://www.poco2.jp/viewer/play.php?partid=735b90b4568125ed6c3f678819b6e058"));

        downloader().pause();
        instance.init();
        downloader().pause();
        downloader().waitUntilFinished();

        assertTrue(instance.images.get(0).equals("http://www.poco2.jp/viewerset/story/0016/0048/00000067/650.jpg"));

        downloader().pause();
        instance.download(directory);
        downloader().pause();
        downloader().waitUntilFinished();

        assertTrue(CRC32.length >= directory.listFiles().length);
        int i = 0;
        for(File file : directory.listFiles())
        {
            assertTrue(CRC32[i] == TestUtils.doChecksum(file));
            i++;

            assertTrue(file.delete());
        }
        assertTrue(directory.delete());
    }
}