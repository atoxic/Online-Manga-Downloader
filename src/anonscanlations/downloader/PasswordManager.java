package anonscanlations.downloader;

import java.io.*;
import java.util.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PasswordManager
{
    public static transient String nicoUsername;
    public static transient char[] nicoPassword;
    
    public static void load()
    {
        nicoUsername = "";
        nicoPassword = new char[0];
        
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
