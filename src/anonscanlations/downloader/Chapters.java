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
    NICONICO("NicoNico"),
    PCVIEWER("PCViewer"),
    PLUGINFREE("PluginFree");

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
