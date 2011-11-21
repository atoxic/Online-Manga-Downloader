package anonscanlations.downloader.downloadjobs;

import java.io.*;
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
    private String data;

    public DownloadJob(String _description)
    {
        description = _description;
        headers = new HashMap<String, String>();
        data = null;
    }

    public final void setPOSTData(String _data)
    {
        data = _data;
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

    protected final void sendPOSTData(URLConnection conn) throws Exception
    {
        if(data != null)
        {
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
        }
    }

    @Override
    public String toString(){ return(description); }

    public abstract void run() throws Exception;
}
