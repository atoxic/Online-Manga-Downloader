#CrochetTime
(not to be confused with T-Time Dot Book)

**Cracked by Nagato.**

##Sites:

+ [Voyager Store](http://voyager-store.com/)

##Download Algorithm:

1. Get the book path, such as

        "amwdc0001".
2. Compute

        "/home/dotbook/rs2_contents/voyager-store_contents/" + [path] + "/" + [path] + "_pc_image_crochet.book.bmit&B" + [8 digit random number]
		Ex: "/home/dotbook/rs2_contents/voyager-store_contents/amwdc0002/amwdc0002_pc_image_crochet.book.bmit&B00006032"
3. Scramble the URL with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L155).
4. GET the book binary file list from "http://shangrila.voyager-store.com/dBmd" with the scrambled URL.
5. Parse through the file list with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L102).
6. For each file name, append it to the path.  Ex:

        "/home/dotbook/rs2_contents/voyager-store_contents/kdsv9784060625410/kdsv9784060625410_pc_image_crochet.book&D&11812&294404000052ff"
7. Scramble it and download it through "http://shangrila.voyager-store.com/dBmd".
8. Decrypt the files with [this code](https://github.com/atoxic/Online-Manga-Downloader/blob/master/src/anonscanlations/downloader/crochettime/CrochetTimeChapter.java#L228).
9. Use zlib to decompress the file to JPEG pages.

##Notes:

+ If you leave off the ".bmit" for the file listing URL, you download an archive containing the entire book.  The format was cracked by Nagato and yet to be implemented in OMD.

        Ex: "/home/dotbook/rs2_contents/voyager-store_contents/amwdc0002/amwdc0002_pc_image_crochet.book&B00006032"
+ For the THUM section in the file listing:  
	Nagato: not sure if this is of any interest to you either but, you can get a thumbnail for all of the pages in the book from the THUM section  
	Nagato: there's a big endian dword right after the section name that gives the size  
	Nagato: then following that it looks like there's a big endian offset  
	Nagato: from the beginning of the chunk + that offset, you will get the thumbnail data  
	Nagato: the jpg size is section size - data offset  
	Nagato: [http://i.imgur.com/5052j.jpg](http://i.imgur.com/5052j.jpg)  
+ There is no authentication necessary.
+ The file name of the thumbnail of the product is the product's path.  Ex: http://voyager-store.com/images/products/amwdc0002.png