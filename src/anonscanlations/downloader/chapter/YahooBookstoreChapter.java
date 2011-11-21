package anonscanlations.downloader.chapter;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

import anonscanlations.downloader.*;
import anonscanlations.downloader.downloadjobs.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class YahooBookstoreChapter extends Chapter
{
    public static final String APPID = "h9Qsaf6kzaJ.EqJij8Ec.J7_NagjVdMdN4Ot0L0TAPf5";
    //http://ebook.yahooapis.jp/v1/epubdata/public?appid=h9Qsaf6kzaJ.EqJij8Ec.J7_NagjVdMdN4Ot0L0TAPf5&goods_id=135393&bookshelf_id=1&public=TRUE&update=FALSE&refereeing=FALSE

    private URL url;
    private String id, ePubURI, publicPath, path, title;
    private ArrayList<String> images;

    public YahooBookstoreChapter(URL _url)
    {
        url = _url;
        id = null;
        ePubURI = null;
        publicPath = null;
        path = null;
        title = null;
        images = new ArrayList<String>();
    }

    public void init() throws Exception
    {
        if(url.getProtocol().equals("http"))
        {
            PageDownloadJob page = new PageDownloadJob("Get page and find id", url, "UTF-8")
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

                    int index = page.indexOf("ybookstore://");
                    if(index == -1)
                        throw new Exception("URI not found");

                    URL uri = new URL(page.substring(index, page.indexOf('"', index)));
                    Map<String, String> params = DownloaderUtils.getQueryMap(uri);
                    id = params.get("i");
                }
            };
            downloader().addJob(page);
        }
        else if(url.getProtocol().equals("ybookstore"))
        {
            Map<String, String> params = DownloaderUtils.getQueryMap(url);
            id = params.get("i");
        }
        else
            throw new Exception("Unknown protocol");

        PageDownloadJob ePubData = new PageDownloadJob("Get info on ePub file", null, "UTF-8")
        {
            @Override
            public void run() throws Exception
            {
                url = new URL("http://ebook.yahooapis.jp/v1/epubdata/public?appid="
                            + APPID + "&goods_id=" + id
                            + "&bookshelf_id=1&public=TRUE&update=FALSE&refereeing=FALSE");

                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");

                parser.setContentHandler(new DefaultHandler()
                {
                    private Stack<String> tags = new Stack<String>();

                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        String tag = tags.peek(), str = new String(ch, start, length);
                        if(tag.equals("epubUri"))
                            ePubURI = str;
                        else if(tag.equals("pageRootPathPublic"))
                            publicPath = str;
                        else if(tag.equals("pageRootPath"))
                            path = str;
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        tags.push(localName);
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName)
                    {
                        tags.pop();
                    }
                });

                parser.parse(is);

                if(path == null)
                    path = publicPath;
            }
        };

        EPubDownloadJob ePub = new EPubDownloadJob("Get ePub", null)
        {
            @Override
            public void run() throws Exception
            {
                url = new URL(ePubURI);
                super.run();
            }

            public void doByteInput(ByteArrayInputStream byte_input) throws Exception {}
            public void doZipEntryInput(ZipInputStream input, ZipEntry e) throws Exception
            {
                if(!e.getName().equals("META-INF/encryption.xml") && !e.getName().equals("OEBPS/content.opf"))
                    return;

                String page = DownloaderUtils.readAllLines(input, "UTF-8");
                
                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(page));
                is.setEncoding("UTF-8");
                parser.setContentHandler(new DefaultHandler()
                {
                    private Stack<String> tags = new Stack<String>();
                    private String key, URI;

                    @Override
                    public void characters(char[] ch, int start, int length)
                    {
                        if(tags.peek().equals("KeyName"))
                            key = new String(ch, start, length);
                    }

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes atts)
                    {
                        tags.push(localName);
                        if(localName.equals("EncryptedData"))
                        {
                            key = null;
                            URI = null;
                        }
                        else if(localName.equals("CipherReference"))
                            URI = atts.getValue("URI");
                        else if(localName.equals("meta")  && atts.getValue("name").equals("ypub:title"))
                            title = atts.getValue("content");
                    }

                    @Override
                    public void endElement(String uri, String localName, String qName)
                    {
                        tags.pop();
                        if(localName.equals("EncryptedData"))
                        {
                            if(key != null && key.equals("1")
                                && URI != null && URI.contains("Images"))
                                images.add(URI);
                        }
                    }
                });
                parser.parse(is);
            }
        };

        
        downloader().addJob(ePubData);
        downloader().addJob(ePub);
    }
    public void download(File directory) throws Exception
    {
        URL base = new URL(path);
        int i = 1;
        for(String image : images)
        {
            String extension = image.substring(image.lastIndexOf('.') + 1);
            YahooBookstoreDownloadJob page = new YahooBookstoreDownloadJob("Page " + i, new URL(base, image),
                                        DownloaderUtils.fileName(directory, title, i, extension));

            downloader().addJob(page);
            i++;
        }
    }
}
