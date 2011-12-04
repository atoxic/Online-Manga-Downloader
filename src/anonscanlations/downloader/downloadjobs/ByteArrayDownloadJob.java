package anonscanlations.downloader.downloadjobs;

import java.io.*;

/** 
 * Downloads to bytes.  Keeps retrying until it finishes downloading.
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class ByteArrayDownloadJob extends JSoupDownloadJob
{
    /**
     * Maximum number of retries.
     */
    protected int maxRetries;
    /**
     * Saved bytes.
     */
    private byte[] bytes;
    public ByteArrayDownloadJob(String _desc, java.net.URL _url)
    {
        super(_desc, _url);
        bytes = null;
        maxRetries = 16;
    }
    @Override
    public void init() throws Exception
    {
        super.init();
        conn.ignoreReadError(true);
    }
    @Override
    public void run() throws Exception
    {
        ByteArrayOutputStream baos = null;
        try
        {
            baos = new ByteArrayOutputStream();
            long contentLength = -1;
            int numTries = 0;
            do
            {
                init();
                if(contentLength != -1)
                    conn.range(baos.size(), -1);
                super.run();
                baos.write(response.bodyAsBytes());
                if(contentLength == -1 && response.hasHeader("Content-Length"))
                    contentLength = Long.parseLong(response.header("Content-Length"));
                numTries++;
            }
            while(numTries < maxRetries && baos.size() < contentLength);
        }
        finally
        {
            if(baos != null)
                baos.close();
        }
        bytes = baos.toByteArray();
    }
    @Override
    public byte[] getBytes()
    {
        return(bytes);
    }
}
