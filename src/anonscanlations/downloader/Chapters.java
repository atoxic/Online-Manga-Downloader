/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public enum Chapters
{
    ACTIBOOK("Actibook"),
    CLUBSUNDAY("Shogakukan"),
    CROCHETTIME("CrochetTime"),
    MANGAONWEB("Manga On Web"),
    NICONICOACE("NicoNico E-Books"),
    NICONICO2("NicoNico Official"),
    NICONICO("NicoNico User-published"),
    PCVIEWER("PCViewer/DOR"),
    PLUGINFREE("PluginFree"),
    POCO("Poco"),
    YAHOOBOOKSTORE("Yahoo Bookstore");

    private final String name;
    Chapters(String _name)
    {
        name = _name;
    }
    public String getName()
    {
        return(name);
    }
}
