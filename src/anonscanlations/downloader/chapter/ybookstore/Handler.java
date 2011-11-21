package anonscanlations.downloader.chapter.ybookstore;

import java.io.*;
import java.net.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class Handler extends URLStreamHandler
{
    protected URLConnection openConnection(URL url) throws IOException
    {
        if(url.getProtocol().equals("ybookstore"))
           return(new YahooBookstoreURLConnection(url));

        URL classicURL = new URL(url.toString());
        return classicURL.openConnection();
    }
    public class YahooBookstoreURLConnection extends URLConnection
    {
        public YahooBookstoreURLConnection(URL _url)
        {
            super(_url);
        }
        @Override
        public void connect() throws IOException { }
    }
}
