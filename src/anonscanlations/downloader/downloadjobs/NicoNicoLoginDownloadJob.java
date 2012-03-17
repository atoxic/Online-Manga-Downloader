package anonscanlations.downloader.downloadjobs;

import java.util.*;
import java.net.*;

/** Requires my forked JSoup (fixed cookie handling)
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoLoginDownloadJob extends JSoupDownloadJob
{
    public String username;
    public char[] password;

    public NicoNicoLoginDownloadJob(String _username, char[] _password)
    {
        super("Login to NicoNico", null);

        username = _username;
        password = _password;
    }
    
    @Override
    public void finalize() throws Throwable
    {
        super.finalize();
        Arrays.fill(password, ' ');
        password = null;
    }

    @Override
    public void run() throws Exception
    {
        if(username == null || password == null)
            throw new Exception("No login");
        url = new URL("https://secure.nicovideo.jp/secure/login?site=seiga");
        addPOSTData("next_url", "/manga/");
        addPOSTData("mail", username);
        addPOSTData("password", new String(password));
        super.run();
    }

    public Map<String, String> getCookies()
    {
        return(response.cookies());
    }
}
