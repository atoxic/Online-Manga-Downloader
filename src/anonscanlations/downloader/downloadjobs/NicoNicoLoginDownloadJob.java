package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.util.*;
import java.net.*;

/** Logs into Nico Nico.  Does use JSoup because there's a bug with multiple cookies of the same name.
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoLoginDownloadJob extends DownloadJob
{
    public String username;
    public Map<String, String> cookies;
    // TODO: make password handling absolutely secure
    public char[] password;

    public NicoNicoLoginDownloadJob(String _username, char[] _password)
    {
        super("Login to NicoNico");

        username = _username;
        password = _password;
        cookies = new HashMap<String, String>();
    }

    public void run() throws Exception
    {
        if(username == null || password == null)
            throw new Exception("No login");

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
            String field = conn.getHeaderField(i);
            if(headerName.equals("Set-Cookie") && field.contains("user_session") && !field.contains("deleted"))
            {
                cookies.put("user_session", field.substring(field.indexOf('=') + 1, field.indexOf(';')));
            }
        }
    }

    public Map<String, String> getCookies()
    {
        return(cookies);
    }
}
