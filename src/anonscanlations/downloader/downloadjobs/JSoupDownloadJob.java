package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class JSoupDownloadJob extends DownloadJob
{
    protected URL url;
    protected Connection conn;
    protected Connection.Response response;

    public JSoupDownloadJob(String _desc, URL _url)
    {
        super(_desc);
    }

    public void run()
    {
        conn = Jsoup.connect(url.toString());
    }
}
