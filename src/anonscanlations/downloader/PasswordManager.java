package anonscanlations.downloader;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PasswordManager
{
    public static transient String nicoUsername, pixivUsername;
    public static transient char[] nicoPassword, pixivPassword;
    
    public static void load()
    {
        nicoUsername = "";
        pixivUsername = "";
        nicoPassword = new char[0];
        pixivPassword = new char[0];
        
        Properties properties = new Properties();
        FileInputStream in = null;
        try
        {
            in = new FileInputStream("passwords.properties");
            properties.load(in);
            for(Map.Entry<Object, Object> propItem : properties.entrySet())
            {
                String key = (String)propItem.getKey();
                String value = (String)propItem.getValue();
                
                if(key.equals("nicoUsername"))
                    nicoUsername = value;
                else if(key.equals("nicoPassword"))
                    nicoPassword = value.toCharArray();
                else if(key.equals("pixivUsername"))
                    pixivUsername = value;
                else if(key.equals("pixivPassword"))
                    pixivPassword = value.toCharArray();
            }
        }
        catch(Exception e)
        {
            DownloaderUtils.error("Loading passwords", e, false);
        }
        finally
        {
            try
            {
                if(in != null)
                    in.close();
            }
            catch(Exception e)
            {
                DownloaderUtils.error("Closing passwords.properties", e, false);
            }
        }
    }
}
