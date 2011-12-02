package anonscanlations.downloader.downloadjobs;

import java.util.*;

/**
 *
 * @author /a/non
 */
public abstract class DownloadJob
{
    protected String description;
    protected final Map<String, String> headers, data;

    public DownloadJob(String _description)
    {
        description = _description;
        headers = new HashMap<String, String>();
        data = new HashMap<String, String>();
    }

    public final void addPOSTData(String _key, String _value)
    {
        data.put(_key, _value);
    }
    public final void addRequestProperty(String _key, String _value)
    {
        headers.put(_key, _value);
    }

    @Override
    public String toString(){ return(description); }

    public abstract void run() throws Exception;
}
