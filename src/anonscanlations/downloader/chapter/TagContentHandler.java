package anonscanlations.downloader.chapter;

import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.bluecast.xml.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class TagContentHandler extends DefaultHandler
{
    protected Stack<String> tags;
    public TagContentHandler()
    {
        tags = new Stack<String>();
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
}
