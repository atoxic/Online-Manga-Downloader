package anonscanlations.downloader;

import java.io.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class POSTDownloadJob extends DownloadJob
{
    protected URL url;
    protected String encoding, page, cookies, data;
    protected HttpURLConnection conn;
    public POSTDownloadJob(String _description, URL _url, String _encoding, String _data)
    {
        this(_description, _url, _encoding, _data, null);
    }
    public POSTDownloadJob(String _description, URL _url, String _encoding, String _data, String _cookies)
    {
        super(_description);
        url = _url;
        encoding = _encoding;
        cookies = _cookies;
        data = _data;
        conn = null;
    }
    public void run() throws Exception
    {
        DownloaderUtils.debug("POSTDownloadJob: " + url);

        conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

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
