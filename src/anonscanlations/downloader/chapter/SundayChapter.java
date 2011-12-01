/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import java.security.*;
import org.jsoup.nodes.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 * TODO: paid shogakukan chapters
 * @author /a/non
 */
public class SundayChapter extends Chapter
{
    private String key1, key2, key3, key4, shd;
    private URL url, keyURL;

    private transient Map<String, String> cookies;
    private transient String otk, server;
    private transient int min, max;
    private transient long timestamp;

    public SundayChapter(URL _url)
    {
        this(_url, null);
    }
    public SundayChapter(URL _url, URL _keyURL)
    {
        keyURL = _keyURL;
        url = _url;
        key1 = null;
        key2 = null;
        key3 = null;
        key4 = null;
        shd = null;
    }

    @Override
    public void getRequiredInfo(LoginManager s) throws Exception
    {
        if(keyURL == null)
        {
            keyURL = new URL(s.getKey().getURL());
        }
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        HashMap<String, String> params = DownloaderUtils.getQueryMap(url);
        key1 = params.get("key1");
        key2 = params.get("key2");
        key3 = params.get("key3");
        key4 = params.get("key4");
        shd = params.get("shd");
        
        SessionDownloadJob session = new SessionDownloadJob();
        MainFileDownloadJob mainFile = new MainFileDownloadJob();
        XMLDownloadJob xml = new XMLDownloadJob();
        
        list.add(session);
        list.add(mainFile);
        list.add(xml);
        return(list);
    }
    
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        String origURL = server + "data/" + key1 + "/" + key2 + "/" + key3 + "/" + key4 + "/bin/";

        for(int i = min; i <= max; i++)
        {
            final File f = DownloaderUtils.fileName(directory, key3 + "_c" + key4, i, "jpg");
            if(f.exists())
                continue;
            
            String imageURL = origURL + key3 + "_" + key4 + "_" + String.format("%04d", i) + ".bin?e=" + timestamp,
                    toHash = "e47ec34c9cf59bca8d8eb865896b74ff88f953d3" + imageURL,
                    hashedURL = "";
            md5.reset();
            md5.update(toHash.getBytes());
            byte[] bytes = md5.digest();
            String tmp = "";
            for(int j = 0; j < bytes.length; j++)
            {
                tmp = (Integer.toHexString(0xFF & bytes[j]));
                if (tmp.length() == 1)
                    hashedURL += "0" + tmp;
                else
                    hashedURL += tmp;
            }

            imageURL += "&h=" + hashedURL;

            PCViewerDownloadJob file = new PCViewerDownloadJob("Page " + i, new URL(imageURL), f);
            list.add(file);
        }

        return(list);
    }

    private class SessionDownloadJob extends JSoupDownloadJob
    {
        public SessionDownloadJob()
        {
            super("Starts a new session", null);
        }
        @Override
        protected void init() throws Exception
        {
            super.init();
            conn.followRedirects(false);
        }
        @Override
        public void run() throws Exception
        {
            if(keyURL == null)
                throw new Exception("No previous URL");
            DownloaderUtils.debug("SessionDJ url: " + keyURL);
            url = keyURL;

            super.run();
            
            SundayChapter.this.cookies = response.cookies();
            if(response.statusCode() >= 300 && response.statusCode() <= 302)
            {
                String location = response.headers().get("Location");
                DownloaderUtils.debug("SessionDJ (Redirect) line: " + location);
                int index = location.indexOf("&otk=");
                otk = location.substring(index + 5, location.indexOf('&', index + 1));
            }
            else
            {
                String page = response.body();
                int index = page.indexOf("&otk=");
                if(index != -1)
                {
                    otk = page.substring(index + 5, page.indexOf('\'', index));
                    if(otk.indexOf('&') != -1)
                    {
                        otk = otk.substring(0, otk.indexOf('&'));
                    }
                }
            }

            DownloaderUtils.debug("SessionDJ otk: " + otk);
        }
    }

    private class MainFileDownloadJob extends JSoupDownloadJob
    {
        public MainFileDownloadJob()
        {
            super("Downloads the main file", null);
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_main.php?key1=" + key1 +
                                "&key2=" + key2 + "&key3=" + key3 +
                                "&key4=" + key4 + "&sp=-1&re=0&shd=" + shd +
                                "&otk=" + otk + "&vo=1");
            DownloaderUtils.debug("MainFileDJ URL: " + this.url);
            setCookies(SundayChapter.this.cookies);

            super.run();
        }
    }

    private class XMLDownloadJob extends JSoupDownloadJob
    {
        public XMLDownloadJob()
        {
            super("Downloads the XML file", null);
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_otk.php?key1=" + key1 +
                            "&key2=" + key2 + "&key3=" + key3 +
                            "&key4=" + key4 + "&sp=-1&re=0&otk=" + otk);
            DownloaderUtils.debug("XMLDJ url: " + this.url);
            setCookies(SundayChapter.this.cookies);

            super.run();

            String page = response.body();
            DownloaderUtils.debug("XMLDJ xml: " + page);
            if(page.equals("NG") || page.contains("pagecount=\"0\""))
                throw new Exception("XML file NG");
            
            Document d = response.parse();
            String serverTimestamp = JSoupUtils.elementAttr(d, "ServerTimestamp", "val"),
                    availTimestamp = JSoupUtils.elementAttr(d, "AvailableTime", "val");
            server = JSoupUtils.elementAttr(d, "ContentsServerPath", "val");
            String list[] = JSoupUtils.elementAttr(d, "SamplePageList", "list").split("-");

            timestamp = Long.parseLong(serverTimestamp) + Long.parseLong(availTimestamp);
            min = Integer.parseInt(list[0]);
            max = Integer.parseInt(list[1]);

            DownloaderUtils.debug("XMLDJ serverTimestamp: " + serverTimestamp);
            DownloaderUtils.debug("XMLDJ availTimestamp: " + availTimestamp);
            DownloaderUtils.debug("XMLDJ timestamp: " + timestamp);
            DownloaderUtils.debug("XMLDJ server: " + server);
            DownloaderUtils.debug("XMLDJ range: " + min + " to " + max);
        }
    }
}
