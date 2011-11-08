package anonscanlations.downloader;

import java.io.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoLoginDownloadJob extends DownloadJob
{
    public String username, cookies;
    // TODO: make password handling absolutely secure
    public char[] password;

    public NicoNicoLoginDownloadJob(String _username, char[] _password)
    {
        super("Login to NicoNico");

        username = _username;
        password = _password;
        cookies = null;
    }

    public void run() throws Exception
    {
        URL url = new URL("https://secure.nicovideo.jp/secure/login?site=seiga");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write("next_url=%2Fmanga%2F&mail=" + username + "&password=");
        wr.write(password);
        wr.flush();

        if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            throw new Exception("404 Page Not Found: " + url);
        String headerName = null;
        for(int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++)
        {
            if(headerName.equals("Set-Cookie") && !conn.getHeaderField(i).contains("deleted"))
            {
                cookies = conn.getHeaderField(i);
            }
        }
    }

    public String getCookies()
    {
        return(cookies);
    }
}