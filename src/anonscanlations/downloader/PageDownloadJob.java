package anonscanlations.downloader;

import java.io.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PageDownloadJob extends DownloadJob
{
    protected URL url;
    protected String encoding, page, cookies;
    protected HttpURLConnection conn;
    public PageDownloadJob(String _description, URL _url, String _encoding)
    {
        this(_description, _url, _encoding, null);
    }
    public PageDownloadJob(String _description, URL _url, String _encoding, String _cookies)
    {
        super(_description);
        url = _url;
        encoding = _encoding;
        cookies = _cookies;
        conn = null;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("PageDownloadJob: " + url);

        conn = (HttpURLConnection) url.openConnection();
        if(cookies != null)
            conn.setRequestProperty("Cookie", cookies);
        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 Page Not Found: " + url);
        BufferedReader stream = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));

	page = "";
        String line;

	while((line = stream.readLine()) != null)
	    page += line;

        stream.close();
    }
}
