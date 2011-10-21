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
    CLUBSUNDAY("Club Sunday"),
    CROCHETTIME("CrochetTime"),
    MANGAONWEB("Manga On Web"),
    NICONICO2("NicoNico Official"),
    NICONICO("NicoNico User-published"),
    PCVIEWER("PCViewer/DOR"),
    PLUGINFREE("PluginFree"),
    POCO("Poco");

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
