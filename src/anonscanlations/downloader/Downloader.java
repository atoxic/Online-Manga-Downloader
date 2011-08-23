/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import anonscanlations.downloader.chapter.SundayChapter;
import java.io.*;
import java.util.*;
import java.net.*;

import anonscanlations.downloader.chapter.*;

/**
 *
 * @author Administrator
 */
public class Downloader extends Thread
{
    private static Downloader currentThread;
    private static List<DownloadJob> jobs;
    
    private boolean die, suspended;
    private final Object finished, waitForJobs;
    private Downloader()
    {
        jobs = Collections.synchronizedList(new ArrayList<DownloadJob>());
        die = false;
        suspended = false;
        finished = new Object();
        waitForJobs = new Object();
    }
    public void addJob(DownloadJob job)
    {
        jobs.add(job);
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
    }
    private void kill()
    {
        die = true;
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
    }
    public void pause()
    {
        suspended = !suspended;
        synchronized(waitForJobs)
        {
            waitForJobs.notify();
        }
    }
    public void waitUntilFinished() throws InterruptedException
    {
        if(jobs.isEmpty())
            return;
        synchronized(finished)
        {
            finished.wait();
        }
    }
    @Override
    public void run()
    {
        while(!die)
        {
            try
            {
                if(jobs.isEmpty())
                    synchronized(finished)
                    {
                        finished.notifyAll();
                    }
                synchronized(waitForJobs)
                {
                    while(!die && (jobs.isEmpty() || suspended))
                    {
                        waitForJobs.wait();
                    }
                }
                
                if(die)
                    break;
                
                DownloadJob job = jobs.remove(0);
                System.out.println("Running job: " + job);
                job.run();
            }
            catch(InterruptedException ie)
            {
            }
            catch(Exception e)
            {
                DownloaderUtils.errorGUI("Error in executing download jobs", e, false);
                jobs.clear();
            }
        }
    }

    public static Downloader getDownloader()
    {
        return(currentThread);
    }

    public static void runChapter(Chapter _chapter, File _directory)
    {
        final Chapter chapter = _chapter;
        final File directory = _directory;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    currentThread.pause();
                    chapter.init();
                    currentThread.pause();

                    currentThread.waitUntilFinished();

                    currentThread.pause();
                    chapter.download(directory);
                    currentThread.pause();

                    currentThread.waitUntilFinished();
                }
                catch(Exception e)
                {
                    DownloaderUtils.errorGUI("Error in spawning download jobs", e, false);
                }
            }
        };
        t.start();
    }
    public static void main(String[] args) throws Exception
    {
        System.setProperty("http.agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.20) Gecko/20110803 Firefox/3.6.20");

        currentThread = new Downloader();
        currentThread.start();

        TempDownloaderFrame frame = new TempDownloaderFrame();
        frame.setVisible(true);

        //ActibookChapter chapter = new ActibookChapter(new URL("http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/_SWF_Window.html"));
        /*
        SundayChapter chapter = new SundayChapter(new URL("http://club.shogakukan.co.jp/dor/pcviewer_main.php?key1=SHWM&key2=takahasiru_001&key3=konshuunos_001&key4=0230-0&sp=-1&re=0&shd=d1b53e031df96f4b93037c3f8c1513415ef9b09d&otk=0f694497ed762f6e7970fff9e2c69529906dc223&vo=1"),
                                                new URL("http://club.shogakukan.co.jp/"));
        // */
        /*
        SundayChapter chapter = new SundayChapter(new URL("http://sokuyomi.jp/dor_sokuyomi/pcviewer_main.php?key1=SHCO&key2=tanakamoto_001&key3=saikyoutor_001&key4=0001-0&sp=-1&re=0&shd=22bc58e0ca503ce2da5120cb06167eed9192fb6e&otk=4162cf06de4a108726b7b6ac5e1912d718d898bd&ls=1"),
                                                new URL("http://sokuyomi.jp/external/viewer/?isbn=4091263747"));
        // */
        //PCViewerChapter chapter = new PCViewerChapter(new URL("http://view.books.yahoo.co.jp/dor/drm/dor_main.php?key1=comicya-iwakutuk01-0010&sp=-1&ad=1&re=0&xmlurl=http://stream01.books.yahoo.co.jp:8001/&shd=a0386be07c30450fcd53081786de81f3ba2da1c5"));
        //PCViewerChapter chapter = new PCViewerChapter(new URL("http://ct.eb-webcomic.com/dor/pcviewer_main.php?key1=EB&key2=tanabekyou_001&key3=senrankagu_001&key4=0001-0&sp=-1&ad=1&re=1&otp=1&xmlurl=http://ct.eb-webcomic.com/stream/&shd=a7e64b0682d07314cbbb5fead053f24c05a73ec8"));
        //PluginFreeChapter chapter = new PluginFreeChapter(new URL("http://futabasha.pluginfree.com/weblish/futabawebhigh/Oniichan_033/index.shtml?Mdn=1&rep=1"));
        //CrochetTimeChapter chapter = new CrochetTimeChapter(new URL("http://voyager-store.com/index.php?main_page=addon&module=ebooks/open_image_crochet&ebooks_id=16470&products_id=11572"));
        //CrochetTimeChapter chapter = new CrochetTimeChapter(new URL("http://voyager-store.com/index.php?main_page=addon&module=ebooks/open_image_crochet&ebooks_id=15275&products_id=11387"));
        //CrochetTimeChapter chapter = new CrochetTimeChapter(new URL("http://comic.bitway.ne.jp/kc/comic_tameshiyomi.html?isbn=9784063725933&t=parchase"));
        //MangaOnWebChapter chapter = new MangaOnWebChapter(new URL("http://mangaonweb.com/viewer.do?ctsn=31029"));
        //NicoNicoChapter chapter = new NicoNicoChapter(new URL("http://seiga.nicovideo.jp/watch/mg22888"), "someusername", "somepassword");

        //runChapter(chapter, new File("D:\\test"));
    }
}
