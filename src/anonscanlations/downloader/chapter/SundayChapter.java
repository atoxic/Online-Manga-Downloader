/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;

import java.security.*;
import org.w3c.dom.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non
 */
public class SundayChapter extends Chapter
{
    public static final KeyURLDialog DIALOG = new KeyURLDialog();

    private String key1, key2, key3, key4, shd;
    private URL url, keyURL;

    private transient String cookies, otk, server;
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

    public ArrayList<DownloadJob> init() throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        HashMap<String, String> params = DownloaderUtils.getQueryMap(url);
        key1 = params.get("key1");
        key2 = params.get("key2");
        key3 = params.get("key3");
        key4 = params.get("key4");
        shd = params.get("shd");
        
        SessionDownloadJob session = new SessionDownloadJob(keyURL);
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

            PCViewerDownloadJob file = new PCViewerDownloadJob("Page " + i, new URL(imageURL), DownloaderUtils.fileName(directory, key3 + "_c" + key4, i, "jpg"));
            list.add(file);
        }

        return(list);
    }

    private class SessionDownloadJob extends DownloadJob
    {
        private URL url;
        public SessionDownloadJob(URL _url)
        {
            super("Starts a new session");
            url = _url;
        }
        public void run() throws Exception
        {
            if(url == null)
            {
                url = new URL(DIALOG.getURL());
            }

            DownloaderUtils.debug("SessionDJ url: " + url);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setInstanceFollowRedirects(false);
            urlConn.connect();
            DownloaderUtils.debug("SessionDJ code: " + urlConn.getResponseCode());

            String headerName = null;
            for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
            {
                if(headerName.equals("Set-Cookie"))
                {
                    cookies = urlConn.getHeaderField(i);
                    int index = cookies.indexOf("SESS");
                    if(index != -1)
                    {
                        DownloaderUtils.debug("SessionDJ cookies: " + cookies);
                        break;
                    }
                }
            }

            if(urlConn.getResponseCode() == 302)
            {
                for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
                {
                    if(headerName.equals("Location"))
                    {
                        String line = urlConn.getHeaderField(i);
                        DownloaderUtils.debug("SessionDJ line: " + line);
                        int index = line.indexOf("&otk=");
                        otk = line.substring(index + 5, line.indexOf('&', index + 1));
                    }
                }
            }
            else
            {
                BufferedReader stream = new BufferedReader(
                                            new InputStreamReader(
                                                urlConn.getInputStream(), "UTF-8"));
                String line;
                int index;
                while((line = stream.readLine()) != null)
                {
                    index = line.indexOf("&otk=");
                    if(index != -1)
                    {
                        DownloaderUtils.debug("SessionDJ line: " + line);
                        otk = line.substring(index + 5, line.indexOf('\'', index));
                        if(otk.indexOf('&') != -1)
                        {
                            otk = otk.substring(0, otk.indexOf('&'));
                        }
                        break;
                    }
                }
                stream.close();
            }

            urlConn.disconnect();

            DownloaderUtils.debug("SessionDJ otk: " + otk);
        }
    }

    private class MainFileDownloadJob extends PageDownloadJob
    {
        public MainFileDownloadJob()
        {
            super("Downloads the main file", null, "UTF-8");
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_main.php?key1=" + key1 +
                                "&key2=" + key2 + "&key3=" + key3 +
                                "&key4=" + key4 + "&sp=-1&re=0&shd=" + shd +
                                "&otk=" + otk + "&vo=1");
            DownloaderUtils.debug("MainFileDJ URL: " + this.url);

            addRequestProperty("Cookie", SundayChapter.this.cookies);

            super.run();
        }
    }

    private class XMLDownloadJob extends PageDownloadJob
    {
        public XMLDownloadJob()
        {
            super("Downloads the XML file", null, "UTF-8");
        }
        @Override
        public void run() throws Exception
        {
            this.url = new URL(SundayChapter.this.url, "pcviewer_otk.php?key1=" + key1 +
                            "&key2=" + key2 + "&key3=" + key3 +
                            "&key4=" + key4 + "&sp=-1&re=0&otk=" + otk);
            DownloaderUtils.debug("XMLDJ url: " + this.url);
            
            addRequestProperty("Cookie", SundayChapter.this.cookies);

            super.run();

            DownloaderUtils.debug("XMLDJ xml: " + page);

            if(page.equals("NG") || page.contains("pagecount=\"0\""))
                throw new Exception("XML file NG");
            
            Document d = DownloaderUtils.makeDocument(page);
            Element root = d.getDocumentElement();

            String serverTimestamp = ((Element)root.getElementsByTagName("ServerTimestamp").item(0))
                                        .getAttribute("val"),
                    availTimestamp = ((Element)root.getElementsByTagName("AvailableTime").item(0))
                                        .getAttribute("val");
            server = ((Element)root.getElementsByTagName("ContentsServerPath").item(0))
                                        .getAttribute("val");
            String list[] = ((Element)root.getElementsByTagName("SamplePageList").item(0))
                                        .getAttribute("list").split("-");

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
