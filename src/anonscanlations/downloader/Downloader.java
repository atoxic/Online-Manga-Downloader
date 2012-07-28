/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import anonscanlations.downloader.chapter.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * @author /a/non
 */
public class Downloader
{
    public static final String VERSION = "Online Manga Downloader 0.1.9 Electric Boogaloo";
    public static final int NUMTHREADS = 3;
    private static TempDownloaderFrame frame;
    private static ThreadPoolExecutor executor;
    // Which status bars are free?
    private static final boolean statusFree[] = new boolean[NUMTHREADS];
    
    private Downloader(){}
    
    public static void status(int index, String s)
    {
        DownloaderUtils.debug("Status: " + s);
        if(frame != null)
            frame.setStatus(index, s);
    }
    
    private static int getFreeStatus()
    {
        int r = -1;
        synchronized(statusFree)
        {
            for(int i = 0; i < NUMTHREADS; i++)
            {
                if(statusFree[i])
                {
                    statusFree[i] = false;
                    r = i;
                    break;
                }
            }
        }
        return(r);
    }
    
    private static void freeStatus(int i)
    {
        synchronized(statusFree)
        {
            statusFree[i] = true;
        }
    }
    
    public static void execute(int index, Chapter c, File baseDirectory) throws Exception
    {
        execute(index, c, baseDirectory, false);
    }
    public static void execute(int index, Chapter c, File baseDirectory, boolean noSubDir) throws Exception
    {
        status(index, "Initializing");
        ArrayList<DownloadJob> jobs = c.init();
        for(DownloadJob j : jobs)
        {
            status(index, j.toString());
            j.run();
        }
        File directory = baseDirectory;
        String title = c.getTitle();
        if(!noSubDir && title != null)
        {
            directory = new File(baseDirectory, DownloaderUtils.sanitizeFileName(title));
            DownloaderUtils.tryMkdirs(directory);
        }
        status(index, "Downloading");
        jobs = c.download(directory);
        for(DownloadJob j : jobs)
        {
            status(index, j.toString());
            j.run();
        }
        status(index, "Finished");
    }
    
    public static void runChapter(Chapter _chapter, File _directory)
    {
        final Chapter c = _chapter;
        final File d = _directory;
        executor.execute(new Runnable()
        {
            public void run()
            {
                int index = getFreeStatus();
                
                try
                {
                    execute(index, c, d);
                }
                catch(Exception e)
                {
                    DownloaderUtils.error("Error while running chapter", e, false);
                    status(index, "Error: " + e.getLocalizedMessage());
                }
                
                freeStatus(index);
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
                int index = getFreeStatus();
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
                
                status(index, "Sorry, couldn't autodetect");
                freeStatus(index);
            }
        });
    }

    public static void initGUI()
    {
        PasswordManager.load();
        
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
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0");
        // in order to handle custom protocols
        System.setProperty("java.protocol.handler.pkgs", "anonscanlations.downloader.chapter");
        
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(NUMTHREADS);
        
        for(int i = 0; i < NUMTHREADS; i++)
            statusFree[i] = true;
    }

    public static void main(String[] args) throws Exception
    {
        // initialize backend
        init();
        
        // initialize frontend
        initGUI();
    }
}