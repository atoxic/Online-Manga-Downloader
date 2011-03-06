#CrochetTime
(not to be confused with T-Time Dot Book)

##Sites:

+ [Voyager Store](http://voyager-store.com/)

##Download Algorithm:

+ Get the book path, such as "amwdc0001_pc_image_crochet".
+ Compute "/home/dotbook/rs2_contents/voyager-store_contents/" + [first part of path] + "/" + [path] + "_pc_image_crochet.book.bmit&B" + [8 digit random number].  Ex: "/home/dotbook/rs2_contents/voyager-store_contents/amwdc0002/amwdc0002_pc_image_crochet.book.bmit&B00006032"
+ Scramble the URL with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L155).
+ GET the book binary file list from "http://shangrila.voyager-store.com/dBmd" with the scrambled URL
+ Parse through the file list with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L102).
+ For each file name, append it to the path.  Ex: "/home/dotbook/rs2_contents/voyager-store_contents/kdsv9784060625410/kdsv9784060625410_pc_image_crochet.book&D&11812&294404000052ff"
+ Scramble it and download it through "http://shangrila.voyager-store.com/dBmd".
+ Decrypt the files with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L228).
+ Use zlib to decompress the file to JPEG pages.

##Notes:

+ If you leave off the ".bmit" for the file listing URL, you download an archive containing the entire book.  The format has yet to be cracked.  Ex: "/home/dotbook/rs2_contents/voyager-store_contents/amwdc0002/amwdc0002_pc_image_crochet.book&B00006032"
+ For the THUM section in the file listing:
Nagato: not sure if this is of any interest to you either but, you can get a thumbnail for all of the pages in the book from the THUM section
Nagato: there's a big endian dword right after the section name that gives the size
Nagato: then following that it looks like there's a big endian offset
Nagato: from the beginning of the chunk + that offset, you will get the thumbnail data
Nagato: the jpg size is section size - data offset
Nagato: http://i.imgur.com/5052j.jpg
