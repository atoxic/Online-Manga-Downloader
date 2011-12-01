package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;
import java.util.zip.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public abstract class EPubDownloadJob extends ByteArrayDownloadJob
{
    protected HttpURLConnection conn;
    public EPubDownloadJob(String _desc, URL _url)
    {
        super(_desc, _url);
        conn = null;
    }

    @Override
    public void run() throws Exception
    {
        super.run();

        ByteArrayInputStream byte_input = null;
        try
        {
            byte_input = new ByteArrayInputStream(bytes);
            doByteInput(byte_input);
        }
        finally
        {
            if(byte_input != null)
                byte_input.close();
        }
        
        ZipInputStream input = null;
        try
        {
            input = new ZipInputStream(new ByteArrayInputStream(bytes));
            ZipEntry e;
            while((e = input.getNextEntry()) != null)
            {
                doZipEntryInput(input, e);
                input.closeEntry();
            }
        }
        finally
        {
            if(input != null)
                input.close();
        }
    }

    public abstract void doByteInput(ByteArrayInputStream byte_input) throws Exception;

    public abstract void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception;
}
