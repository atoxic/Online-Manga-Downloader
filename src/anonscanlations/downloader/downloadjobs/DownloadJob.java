package anonscanlations.downloader.downloadjobs;

import java.util.*;
import java.net.*;

/**
 *
 * @author /a/non
 */
public abstract class DownloadJob
{
    protected String description;
    private Map<String, String> headers;
    public DownloadJob(String _description)
    {
        description = _description;
        headers = new HashMap<String, String>();
    }
    public final void addRequestProperty(String _key, String _value)
    {
        headers.put(_key, _value);
    }

    protected final void setRequestProperties(URLConnection conn)
    {
        for(Map.Entry<String, String> e : headers.entrySet())
            conn.setRequestProperty(e.getKey(), e.getValue());
    }

    @Override
    public String toString(){ return(description); }

    public abstract void run() throws Exception;
}
