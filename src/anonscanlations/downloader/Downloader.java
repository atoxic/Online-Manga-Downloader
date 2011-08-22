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
            catch(Exception ex)
            {
                DownloaderUtils.error("Error in Downloader", ex, true);
            }
        }
    }

    public static Downloader getDownloader()
    {
        return(currentThread);
    }
    public static void main(String[] args) throws Exception
    {
        currentThread = new Downloader();
        currentThread.start();

        //ActibookChapter chapter = new ActibookChapter(new URL("http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/_SWF_Window.html"));
        /*
        SundayChapter chapter = new SundayChapter(new URL("http://club.shogakukan.co.jp/dor/pcviewer_main.php?key1=SHWM&key2=mitudataku_001&key3=konshuunos_001&key4=0229-0&sp=-1&re=0&shd=b3787698976e44ec3369a75f37e8ca972fa27a9a&otk=8a44629df43cbd6c3549bbc9a98e0cc091846285"),
                                                new URL("http://club.shogakukan.co.jp/"));
        // */
        /*
        SundayChapter chapter = new SundayChapter(new URL("http://sokuyomi.jp/dor_sokuyomi/pcviewer_main.php?key1=SHCO&key2=tanakamoto_001&key3=saikyoutor_001&key4=0001-0&sp=-1&re=0&shd=22bc58e0ca503ce2da5120cb06167eed9192fb6e&otk=4162cf06de4a108726b7b6ac5e1912d718d898bd&ls=1"),
                                                new URL("http://sokuyomi.jp/external/viewer/?isbn=4091263747"));
        // */
        //PCViewerChapter chapter = new PCViewerChapter(new URL("http://view.books.yahoo.co.jp/dor/drm/dor_main.php?key1=comicya-iwakutuk01-0010&sp=-1&ad=1&re=0&xmlurl=http://stream01.books.yahoo.co.jp:8001/&shd=a0386be07c30450fcd53081786de81f3ba2da1c5"));
        //PCViewerChapter chapter = new PCViewerChapter(new URL("http://ct.eb-webcomic.com/dor/pcviewer_main.php?key1=EB&key2=tanabekyou_001&key3=senrankagu_001&key4=0001-0&sp=-1&ad=1&re=1&otp=1&xmlurl=http://ct.eb-webcomic.com/stream/&shd=a7e64b0682d07314cbbb5fead053f24c05a73ec8"));
        PluginFreeChapter chapter = new PluginFreeChapter(new URL("http://futabasha.pluginfree.com/weblish/futabawebhigh/Oniichan_033/index.shtml?Mdn=1&rep=1"));

        System.out.println("Init");
        currentThread.pause();
        chapter.init();
        currentThread.pause();

        //*
        // wait until init is finished
        currentThread.waitUntilFinished();
        
        System.out.println("Download");
        currentThread.pause();
        chapter.download(new File("D:\\test\\"));
        currentThread.pause();
        // */

        // wait until download is finished
        currentThread.waitUntilFinished();
        currentThread.kill();
        // */
    }
}
