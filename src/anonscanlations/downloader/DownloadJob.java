package anonscanlations.downloader;

/**
 *
 * @author /a/non
 */
public abstract class DownloadJob
{
    protected String description;
    public DownloadJob(String _description)
    {
        description = _description;
    }
    @Override
    public String toString(){ return(description); }

    public abstract void run() throws Exception;
}
