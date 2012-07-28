package anonscanlations.downloader.downloadjobs;

import java.util.*;
import java.net.*;

/** Requires my forked JSoup (fixed cookie handling)
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class LoginDownloadJob extends JSoupDownloadJob
{
    public enum Type
    {
      PIXIV, NICONICO;  
    };
    
    protected Type type;
    protected transient String username;
    protected transient char[] password;

    public LoginDownloadJob(String _desc, Type _type, String _username, char[] _password)
    {
        super(_desc, null);
        
        type = _type;
        username = _username;
        password = _password;
    }
    
    @Override
    public void finalize() throws Throwable
    {
        super.finalize();
        cleanup();
    }
    
    private void cleanup()
    {
        if(password != null)
        {
            Arrays.fill(password, ' ');
            password = null;
        }
    }

    @Override
    public void run() throws Exception
    {
        if(username == null || password == null)
            throw new Exception("No login");
        
        if(type == Type.PIXIV)
        {
            url = new URL("http://www.pixiv.net/login.php");
            addPOSTData("mode", "login");
            addPOSTData("pixiv_id", username);
            addPOSTData("pass", new String(password));
        }
        else
        {
            url = new URL("https://secure.nicovideo.jp/secure/login?site=seiga");
            addPOSTData("next_url", "/manga/");
            addPOSTData("mail", username);
            addPOSTData("password", new String(password));
        }
        
        super.run();
        
        cleanup();
    }
}
