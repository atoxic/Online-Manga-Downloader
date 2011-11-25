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
    FLIPPER3("Flipper3"),
    MANGAONWEB("Manga On Web"),
    NICONICOACE("NicoNico E-Books"),
    NICONICO2("NicoNico Official"),
    NICONICO("NicoNico User-published"),
    PCVIEWER("PCViewer/DOR"),
    PLUGINFREE("PluginFree"),
    POCO("Poco"),
    WEBYOUNGJUMP("Web Young Jump"),
    YAHOOBOOKSTORE("Yahoo Bookstore");

    private final String name;
    private Chapters(String _name)
    {
        name = _name;
    }
    public String getName()
    {
        return(name);
    }
}
