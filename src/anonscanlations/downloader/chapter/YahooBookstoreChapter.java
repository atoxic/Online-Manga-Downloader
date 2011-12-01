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
    private Map<String, String> params;
    private String ePubURI, publicPath, path, title;
    private ArrayList<String> images;

    public YahooBookstoreChapter(URL _url)
    {
        url = _url;
        ePubURI = null;
        publicPath = null;
        path = null;
        title = null;
        images = new ArrayList<String>();
    }

    public ArrayList<DownloadJob> init() throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();

        if(url.getProtocol().equals("http"))
        {
            JSoupDownloadJob page = new JSoupDownloadJob("Get page and find id", url)
            {
                @Override
                public void run() throws Exception
                {
                    super.run();

                    String page = response.body();
                    int index = page.indexOf("ybookstore://");
                    if(index == -1)
                        throw new Exception("URI not found");

                    URL uri = new URL(page.substring(index, page.indexOf('"', index)));
                    params = DownloaderUtils.getQueryMap(uri);
                }
            };
            list.add(page);
        }
        else if(url.getProtocol().equals("ybookstore"))
        {
            params = DownloaderUtils.getQueryMap(url);
        }
        else
            throw new Exception("Unknown protocol");

        JSoupDownloadJob ePubData = new JSoupDownloadJob("Get info on ePub file", null)
        {
            @Override
            public void run() throws Exception
            {
                // TODO: make this compatibile with bought products
                url = new URL("http://ebook.yahooapis.jp/v1/epubdata/public?appid="
                            + APPID + "&goods_id=" + params.get("i")
                            + "&bookshelf_id=1&public=TRUE&update=FALSE&refereeing=FALSE");

                super.run();

                Piccolo parser = new Piccolo();
                InputSource is = new InputSource(new StringReader(response.body()));
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
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);

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

                DownloaderUtils.debug("=== File: " + e.getName() + " ===");

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
                parser.setEntityResolver(new DefaultEntityResolver());
                parser.parse(is);
            }
        };

        list.add(ePubData);
        list.add(ePub);
        return(list);
    }
    public ArrayList<DownloadJob> download(File directory) throws Exception
    {
        ArrayList<DownloadJob> list = new ArrayList<DownloadJob>();
        URL base = new URL(path);
        int i = 1;
        for(String image : images)
        {
            String extension = image.substring(image.lastIndexOf('.') + 1);
            File f = DownloaderUtils.fileName(directory, title, i, extension);
            if(f.exists())
            {
                i++;
                continue;
            }
            
            YahooBookstoreDownloadJob page = new YahooBookstoreDownloadJob("Page " + i, new URL(base, image), f);
            list.add(page);
            i++;
        }
        return(list);
    }
}
