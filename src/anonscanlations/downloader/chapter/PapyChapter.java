package anonscanlations.downloader.chapter;

import java.io.*;
import java.util.*;
import java.net.*;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import org.xml.sax.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class PapyChapter extends Chapter
{
    private static final Exception E = new Exception("PDF URL not found");
    
    private URL url;
    private String pdf, title;
    public PapyChapter(URL _url)
    {
        url = _url;
        pdf = null;
        title = null;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        JSoupDownloadJob page = new JSoupDownloadJob("Get page", url)
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                int index = response.body().indexOf("src:");
                if(index == -1)     throw E;
                index = response.body().indexOf('"', index) + 1;
                if(index == 0)      throw E;
                int endIndex = response.body().indexOf('"', index);
                if(endIndex == -1)  throw E;
                pdf = response.body().substring(index, endIndex);
            }
        };
        list.add(page);
        return(list);
    }
    
    @Override
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        final File finalDirectory = directory;
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(new ByteArrayDownloadJob("All Pages", new URL(pdf))
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                PdfReader r = new PdfReader(getBytes());
                parseMetadata(r.getMetadata());
                PdfReaderContentParser parser = new PdfReaderContentParser(r);
                RenderListener listener = new RenderListener()
                {
                    private int i = 1;
                    public void renderImage(ImageRenderInfo renderInfo)
                    {
                        try
                        {
                            DownloaderUtils.safeWrite(renderInfo.getImage().getImageAsBytes(), 
                                    DownloaderUtils.fileName(finalDirectory, title, i, renderInfo.getImage().getFileType()));
                        }
                        catch(IOException e)
                        {
                            DownloaderUtils.error("PDF image extraction failed", e, false);
                        }
                        finally
                        {
                            i++;
                        }
                    }
                    
                    public void beginTextBlock(){}
                    public void endTextBlock(){}
                    public void renderText(TextRenderInfo renderInfo){}
                };
                for(int i = 1; i <= r.getNumberOfPages(); i++)
                    parser.processContent(i, listener);
            }
        });
        return(list);
    }
    
    private void parseMetadata(byte[] b) throws Exception
    {
        Piccolo parser = new Piccolo();
        InputSource is = new InputSource(new ByteArrayInputStream(b));
        is.setEncoding("UTF-8");

        parser.setContentHandler(new TagContentHandler()
        {
            @Override
            public void characters(char[] ch, int start, int length)
            {
                if(tags.peek().equals("li") && tags.contains("title"))
                    title = new String(ch, start, length);
            }
        });
        parser.setEntityResolver(new DefaultEntityResolver());
        parser.parse(is);
    }
}
