package anonscanlations.downloader.mangaonweb;

import java.io.*;
import java.net.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class MangaOnWebChapter extends Chapter
{
    private String ctsn, title;
    private int total;

    private transient String cookies, cdn, crcod;

    public MangaOnWebChapter(){}
    public MangaOnWebChapter(String ctsn, String title)
    {
        this.ctsn = ctsn;
        this.title = title;
        this.total = -1;
    }

    public String getTitle()
    {
        return(title);
    }

    public int getTotal()
    {
        return(total);
    }

    public boolean init(){ return(true); }

    private boolean handshake() throws Exception
    {
        URL homePage = new URL("http://mangaonweb.com/viewer.do?ctsn=" + ctsn);

        HttpURLConnection urlConn = (HttpURLConnection)homePage.openConnection();
        urlConn.connect();
        if(urlConn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            return(false);

        // save the cookie
        String headerName = null;
        for(int i = 1; (headerName = urlConn.getHeaderFieldKey(i)) != null; i++)
        {
            if(headerName.equals("Set-Cookie"))
            {
                cookies = urlConn.getHeaderField(i);
            }
        }

        // save cdn and crcod
        String page = "", line;
        BufferedReader stream = new BufferedReader(
                                    new InputStreamReader(
                                        urlConn.getInputStream(), "UTF-8"));
        while((line = stream.readLine()) != null)
	    page += line;

        cdn = param(page, "cdn");
        crcod = param(page, "crcod");

        return(true);
    }

    public ArrayList<String> parseXML() throws Exception
    {
        ArrayList<String> ret = new ArrayList<String>();

        handshake();

        URL url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=book.xml&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));
        String page = DownloaderUtils.getPage(url.toString(), "UTF-8", cookies);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(page));
        Document d = builder.parse(is);
        Element doc = d.getDocumentElement();

        NodeList pages = doc.getElementsByTagName("page");
        total = pages.getLength();
        for(int i = 0; i < pages.getLength(); i++)
        {
            Element e = (Element)pages.item(i);
            ret.add(e.getAttribute("path"));
        }

        return(ret);
    }

    public boolean download(DownloadListener dl) throws Exception
    {
        // 1) get crcod, cdn, and cookies
        handshake();

        // 2) get XML
        ArrayList<String> paths = parseXML();
        dl.setTotal(getTotal());

        // 3) get pages
        byte[] key = {99, 49, 51, 53, 100, 54, 56, 56, 57, 57, 99, 56, 50, 54, 99, 101, 100, 55, 99, 52, 57, 98, 99, 55, 54, 97, 97, 57, 52, 56, 57, 48};
        BlowFishKey bfkey = new BlowFishKey(key);
        for(int i = 0; i < paths.size(); i++)
        {
            if(dl.isDownloadAborted())
                return(true);

            // rid is just a random number from 0-9999
            URL url = new URL("http://mangaonweb.com/page.do?cdn=" + cdn + "&cpn=" + paths.get(i) + "&crcod=" + crcod + "&rid=" + (int)(Math.random() * 10000));

            byte[] encrypted = downloadByteArray(url);
            bfkey.decrypt(encrypted, 0);

            RandomAccessFile output = new RandomAccessFile(dl.downloadPath(this, i), "rw");
            output.write(encrypted);
            output.close();

            dl.downloadIncrement(this);
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
        return(buf);
    }

    // get something like "param=return&"
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
