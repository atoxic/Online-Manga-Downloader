/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import anonscanlations.downloader.chapter.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * @author /a/non
 */
public class Downloader
{
    public static final String VERSION = "Online Manga Downloader 0.1.6.7 Test";
    public static final int NUMTHREADS = 3;
    private static TempDownloaderFrame frame;
    private static ThreadPoolExecutor executor;
    
    private Downloader(){}
    
    public static void status(int index, String s)
    {
        DownloaderUtils.debug("Status: " + s);
        if(frame != null)
            frame.setStatus(index, s);
    }
    
    public static void execute(int index, Chapter c, File d) throws Exception
    {
        status(index, "Initializing");
        ArrayList<DownloadJob> jobs = c.init();
        for(DownloadJob j : jobs)
        {
            status(index, j.toString());
            j.run();
        }
        status(index, "Downloading");
        jobs = c.download(d);
        for(DownloadJob j : jobs)
        {
            status(index, j.toString());
            j.run();
        }
        status(index, "Finished");
    }
    
    private static int getIndex()
    {
        int index = executor.getActiveCount() - 1;
        if(index < 0)
            index = 0;
        else if(index >= NUMTHREADS)
            index = NUMTHREADS - 1;
        DownloaderUtils.debug("Thread Index: " + index);
        return(index);
    }
    
    public static void runChapter(Chapter _chapter, File _directory)
    {
        final Chapter c = _chapter;
        final File d = _directory;
        executor.execute(new Runnable()
        {
            public void run()
            {
                int index = getIndex();
                
                try
                {
                    execute(index, c, d);
                }
                catch(Exception e)
                {
                    DownloaderUtils.error("Error while running chapter", e, false);
                    status(index, "Error: " + e.getLocalizedMessage());
                }
            }
        });
    }

    public static void autodetectChapter(ArrayList<Chapter> _chapters, File _directory)
    {
        final ArrayList<Chapter> chapters = _chapters;
        final File d = _directory;
        executor.execute(new Runnable()
        {
            public void run()
            {
                int index = getIndex();
                LoginManager s = new LoginManager();
                
                for(Chapter c : chapters)
                {
                    try
                    {
                        Object lock = s.showDialog(c);
                        if(lock != null)
                        {
                            synchronized(lock)
                            {
                                lock.wait();
                            }
                        }
                        c.getRequiredInfo(s);
                        
                        execute(index, c, d);
                        return;
                    }
                    catch(Exception e)
                    {
                        DownloaderUtils.error("Handler failed: " + c.getClass(),
                                e instanceof ExecutionException ? 
                                    ((ExecutionException)e).getException() :
                                    e, false);
                        status(index, "Handler \"" + c.getClass() + "\" error: " + e.getLocalizedMessage());
                    }
                }
            }
        });
    }

    public static void initGUI()
    {
        // Try to use native look and feel
        try
        {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            // couldn't find native look and feel: it couldn't be helped
            DownloaderUtils.error("Could not set look and feel to native LnF", e, false);
        }
        frame = TempDownloaderFrame.getFrame();
        frame.setVisible(true);
    }
    
    public static void init() throws Exception
    {
        // unnecessary, but just to appear like a real viewer
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0) Gecko/20100101 Firefox/8.0");
        // in order to handle custom protocols
        System.setProperty("java.protocol.handler.pkgs", "anonscanlations.downloader.chapter");
        
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(3);
    }

    public static void main(String[] args) throws Exception
    {
        // initialize backend
        init();
        
        // initialize frontend
        initGUI();
        
        runChapter(new MCSChapter(new java.net.URL("http://comic-rush.jp/viewer/sample?contentId=398&product_id=0000000008-00-0000")), new File("D:\\test"));
    }
}