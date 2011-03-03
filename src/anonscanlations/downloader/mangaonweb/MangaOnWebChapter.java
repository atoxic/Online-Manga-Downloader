package anonscanlations.downloader.mangaonweb;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebChapter extends Chapter
{
    private transient String ctsn = "31073", cookies;

    public String getTitle()
    {
        return("001");
    }

    public int getMin()
    {
        return(1);
    }
    public int getMax()
    {
        return(137);
    }

    public boolean download(DownloadListener dl) throws Exception
    {
        /* 1) get crcod and cdn (session code)
         * 2) get xml (unneeded)
         * 3) get pages and decode them
         */
        URL homePage = new URL("http://mangaonweb.com/viewer.do?ctsn=" + ctsn);

        HttpURLConnection urlConn = (HttpURLConnection)homePage.openConnection();
        urlConn.connect();

        String headerName = null;
        for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
        {
            if(headerName.equals("Set-Cookie"))
            {
                cookies = urlConn.getHeaderField(i);
                System.out.println("cookies: " + cookies);
            }
        }

        String page = "", line;
        BufferedReader stream = new BufferedReader(
                                    new InputStreamReader(
                                        urlConn.getInputStream(), "UTF-8"));
        while((line = stream.readLine()) != null)
	    page += line;

        String cdn = param(page, "cdn");
        String crcod = param(page, "crcod");

        System.out.println("cdn: " + cdn);
        System.out.println("crcod: " + crcod);

        DownloaderUtils.downloadByteArray(new URL("http://mangaonweb.com/pages/viewer/BKViewer.swf?v114"));

        byte[] key = {99, 49, 51, 53, 100, 54, 56, 56, 57, 57, 99, 56, 50, 54, 99, 101, 100, 55, 99, 52, 57, 98, 99, 55, 54, 97, 97, 57, 52, 56, 57, 48};
        BlowFishKey bfkey = new BlowFishKey(key);
        for(int i = 1; i < 137; i++)
        {
            if(dl.isDownloadAborted())
                return(true);

            URL url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=page_" + i + ".jpg&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));
            //System.out.println("url: " + url);

            byte[] encrypted = downloadByteArray(url);
            bfkey.decrypt(encrypted, 0);

            RandomAccessFile output = new RandomAccessFile(dl.downloadPath(this, i), "rw");
            output.write(encrypted);
            output.close();

            dl.downloadProgressed(this, i);
        }

        dl.downloadFinished(this);

        return(true);
    }

    private byte[] downloadByteArray(URL url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Cookie", cookies);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(null);
        
        InputStream in = conn.getInputStream();
        byte[] buf = new byte[conn.getContentLength()];
        int read, offset = 0;
        while((read = in.read(buf, offset, buf.length - offset)) != -1)
        {
            offset += read;
        }

        System.out.println("length: " + buf.length);
        return(buf);
    }

    private static String param(String page, String param)
    {
        int index = 0, endIndex;
        index = page.indexOf(param + "=");
        endIndex = page.indexOf('&', index);
        if(page.indexOf('"', index) < endIndex)
            endIndex = page.indexOf('"', index);
        return(page.substring(index + param.length() + 1, endIndex));
    }
}
