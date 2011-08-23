#Online Manga Downloader
##What is it?

It's a downloader for manga that are viewable but not downloadable from the official source.  Current, this downloader supports downloading from:

+ [GanGan Online](http://www.square-enix.com/jp/magazine/ganganonline/) (ActiBook)
+ [Yahoo! Comic](http://comics.yahoo.co.jp/magazine/) (PCViewer)
+ [Club Sunday](http://club.shogakukan.co.jp/) (PCViewer + special encryption)
+ [Famitsu Comic Clear (only se\_\* right now)](http://www.famitsu.com/comic_clear/cl_list/) (se\_\* = PCViewer, co\_\* = HTML, yo\_\* = ???)
+ [Comic High](http://comichigh.jp/webcomic.html) (PluginFree)
+ [Comic Gekkin](http://www.comic-gekkin.com/) (PluginFree)
+ [Manga On Web](http://mangaonweb.com/) (Hub)
+ [NicoNico](http://seiga.nicovideo.jp/manga/) (NicoNico)
+ [Voyager Store (only CrochetTime right now)](http://voyager-store.com/) ([CrochetTime](https://github.com/atoxic/Online-Manga-Downloader/blob/master/docs/crochettime.md), T-Time)

##How do I use it?

Make sure that you have [JRE6 or newer](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed.

Download the binary (or compile from source), and run it.  Enter the URL for the viewer.  For example:
+ [http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/_SWF_Window.html](http://www.square-enix.com/jp/magazine/ganganonline/comic/ryushika/viewer/001/_SWF_Window.html)
+ [http://seiga.nicovideo.jp/watch/mg25108](http://seiga.nicovideo.jp/watch/mg25108)
+ [http://futabasha.pluginfree.com/weblish/futabawebhigh/Oniichan_033/transit2.html?Mdn=1](http://futabasha.pluginfree.com/weblish/futabawebhigh/Oniichan_033/transit2.html?Mdn=1)
+ [http://view.books.yahoo.co.jp/dor/drm/dor_main.php?key1=comicya-iwakutuk01-0010&sp=-1&ad=1&re=0&xmlurl=http://stream01.books.yahoo.co.jp:8001/&shd=a0386be07c30450fcd53081786de81f3ba2da1c5](http://view.books.yahoo.co.jp/dor/drm/dor_main.php?key1=comicya-iwakutuk01-0010&sp=-1&ad=1&re=0&xmlurl=http://stream01.books.yahoo.co.jp:8001/&shd=a0386be07c30450fcd53081786de81f3ba2da1c5)

For Club Sunday, you need to enter the page you found the chapter on for the downloader to acquire a correct OTK key.  For example, if you want chapter 1 of [this](http://club.shogakukan.co.jp/magazine/SH_CSNDY/choudokyuu_001/detail/), you should to enter something like this:
+ Viewer URL: http://club.shogakukan.co.jp/dor/pcviewer_main.php?key1=SHWM&key2=azumatakes_001&key3=choudokyuu_001&key4=0001-0&sp=-1&re=0&shd=e255451bc193d8388067ad31e2923665c88efc5d&otk=80afa9fa06f0ca23b0a91f5d8271a8beea4d4a41&vo=1  (otk=... will be different)
+ Previous Page: http://club.shogakukan.co.jp/magazine/SH_CSNDY/choudokyuu_001/detail/

For NicoNico, you need to enter login details for a valid NicoNico account.