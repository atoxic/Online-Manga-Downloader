package anonscanlations.downloader;

import org.jsoup.*;
import org.jsoup.nodes.*;

/** Helper methods for JSoup
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class JSoupUtils
{
    public static String elementText(Element root, String selector)
    {
        for(Element e : root.select(selector))
            return(e.ownText());
        return(null);
    }
    public static int elementTextInt(Element root, String selector)
    {
        return(Integer.parseInt(elementText(root, selector)));
    }
    public static float elementTextFloat(Element root, String selector)
    {
        return(Float.parseFloat(elementText(root, selector)));
    }
    public static String elementAttr(Element root, String selector, String attr)
    {
        for(Element e : root.select(selector))
            return(e.attr(attr));
        return(null);
    }
}
