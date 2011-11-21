package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PageDownloadJob extends DownloadJob
{
    protected URL url;
    protected String encoding, page;
    protected HttpURLConnection conn;
    public PageDownloadJob(String _description, URL _url, String _encoding)
    {
        super(_description);
        url = _url;
        encoding = _encoding;
        conn = null;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("PageDownloadJob: " + url);

        conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn);
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
