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
            new long[]{1664894238L, 235207973L, 958148213L, 2885041639L,
                    1735256010L, 361631088L, 562778741L, 1674556494L,
                    2406686717L, 685363998L, 934753219L, 3073342046L,
                    2704109636L, 1906777089L};

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
        PocoChapter instance = new PocoChapter(new URL("http://www.poco2.jp/viewer/play.php?partid=f4b9ec30ad9f68f89b29639786cb62ef"));
        Downloader.getDownloader().pause();
        Downloader.getDownloader().addJobs(instance.init());
        Downloader.getDownloader().unpause();
        Downloader.getDownloader().waitUntilFinished();

        assertTrue(instance.images.get(0).equals("http://www.poco2.jp/viewerset/story/0033/0069/00000094/1120.jpg"));
    }

    /**
     * Test of download method, of class PocoChapter.
     */
    @Test
    public void testDownload() throws Exception
    {
        File directory = TestUtils.createTempDirectory();
        PocoChapter instance = new PocoChapter(new URL("http://www.poco2.jp/viewer/play.php?partid=f4b9ec30ad9f68f89b29639786cb62ef"));

        Downloader.getDownloader().pause();
        Downloader.getDownloader().addJobs(instance.init());
        Downloader.getDownloader().unpause();
        Downloader.getDownloader().waitUntilFinished();

        assertTrue(instance.images.get(0).equals("http://www.poco2.jp/viewerset/story/0033/0069/00000094/1120.jpg"));

        Downloader.getDownloader().pause();
        Downloader.getDownloader().addJobs(instance.download(directory));
        Downloader.getDownloader().unpause();
        Downloader.getDownloader().waitUntilFinished();

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