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
public class BookendChapter extends Chapter
{
    private String pdf;
    
    public BookendChapter(URL _url)
    {
        super(_url);
        
        pdf = null;
    }
    
    @Override
    public ArrayList<DownloadJob> init() throws Exception
    {
        DownloaderUtils.checkHTTP(url);
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        JSoupDownloadJob page = new JSoupDownloadJob("Get page", url)
        {
            // Todo: more robust URL extraction?
            @Override
            public void run() throws Exception
            {
                super.run();
                DownloaderUtils.debug("File: " + response.url().getFile());
                if(response.url().getFile().contains("install.html"))
                {
                    this.url = new URL(DownloaderUtils.getQueryMap(response.url()).get("successurl"));
                    DownloaderUtils.debug("New URL: " + this.url);
                    super.init();
                    super.run();
                }
                
                int index = response.body().indexOf("src:");
                if(index == -1)     throw new Exception("PDF URL not found");
                index = response.body().indexOf('"', index) + 1;
                if(index == 0)      throw new Exception("PDF URL not found");
                int endIndex = response.body().indexOf('"', index);
                if(endIndex == -1)  throw new Exception("PDF URL not found");
                pdf = response.body().substring(index, endIndex);
            }
        };
        list.add(page);
        return(list);
    }
    
    @Override
    public ArrayList<DownloadJob> download(File baseDirectory) throws Exception
    {
        final File fBaseDirectory = baseDirectory;
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        list.add(new ByteArrayDownloadJob("All Pages", new URL(pdf))
        {
            @Override
            public void run() throws Exception
            {
                super.run();
                PdfReader r = new PdfReader(getBytes());
                parseMetadata(r.getMetadata());
                
                final File directory = new File(fBaseDirectory, DownloaderUtils.sanitizeFileName(title));
                DownloaderUtils.tryMkdirs(directory);
                
                PdfReaderContentParser parser = new PdfReaderContentParser(r);
                RenderListener listener = new RenderListener()
                {
                    private int i = 1;
                    public void renderImage(ImageRenderInfo renderInfo)
                    {
                        try
                        {
                            DownloaderUtils.safeWrite(renderInfo.getImage().getImageAsBytes(), 
                                    DownloaderUtils.fileName(directory, i, renderInfo.getImage().getFileType()));
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
