package anonscanlations.downloader.chapter.ybookstore;

import java.io.*;
import java.net.*;

/**
 * Handles ybookstore protocol.  Used for Yahoo Bookstore URLs
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class Handler extends URLStreamHandler
{
    /**
     * Opens a connection.
     * @param url   URL to open
     * @return      YahooBookstoreURLConnection if url's protocol is ybookstore, other stuff otherwise.
     * @throws      IOException When error happens.
     */
    protected URLConnection openConnection(URL url) throws IOException
    {
        if(url.getProtocol().equals("ybookstore"))
           return(new YahooBookstoreURLConnection(url));

        URL classicURL = new URL(url.toString());
        return(classicURL.openConnection());
    }
    /**
     * Stub URLConnection implementation
     */
    public class YahooBookstoreURLConnection extends URLConnection
    {
        /**
         * Initialize.
         * @param _url  Sent to superclass constructor
         */
        public YahooBookstoreURLConnection(URL _url)
        {
            super(_url);
        }
        /**
         * Try to connect.  Guaranteed to throw IOException.
         * @throws  IOException Always thrown.
         */
        @Override
        public void connect() throws IOException
        {
            throw new IOException("Trying to connect using YahooBookstoreURLConnection");
        }
    }
}
