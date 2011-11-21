package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class POSTDownloadJob extends DownloadJob
{
    protected URL url;
    protected String encoding, page, data;
    protected HttpURLConnection conn;
    public POSTDownloadJob(String _description, URL _url, String _encoding, String _data)
    {
        super(_description);
        url = _url;
        encoding = _encoding;
        data = _data;
        conn = null;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("POSTDownloadJob: " + url);

        conn = (HttpURLConnection) url.openConnection();
        setRequestProperties(conn);
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        
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
