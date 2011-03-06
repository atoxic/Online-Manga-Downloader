/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader.crochettime;

import java.io.*;
import java.util.*;

import anonscanlations.downloader.*;

/**
 *
 * @author /a/non
 */
public class CrochetTimeChapter
{
    private transient String path = "amwdc0002";

    private static final int[] SCRAMBLE_KEY_TABLE = new int[]{
        0xD4, 0x92, 0xA8, 0x7D, 0x0F, 0x78, 0x3E, 0x57, 0x5B, 0x2E, 0x09, 0x58, 0x44, 0xA6, 0x46, 0xA7,
        0x76, 0xAD, 0xAC, 0xF0, 0x7C, 0x57, 0xAC, 0x48, 0xB5, 0xCB, 0xD6, 0xCF, 0xC4, 0xC4, 0x3B, 0x98,
        0x56, 0xE4, 0x15, 0x66, 0x5C, 0x54, 0xBE, 0xB7, 0x83, 0xC7, 0x10, 0xC7, 0x6E, 0x56, 0x6E, 0xE5,
        0x04, 0x1B, 0xD5, 0x80, 0x72, 0x81, 0xC9, 0x27, 0x4D, 0xA0, 0xF7, 0x11, 0x64, 0x33, 0xA9, 0xBB,
        0x17, 0xBF, 0x21, 0x73, 0x14, 0xDF, 0x2B, 0x97, 0xA7, 0x3B, 0x5E, 0x15, 0x92, 0xCD, 0xFB, 0x96,
        0xE9, 0xD0, 0x17, 0x5B, 0x52, 0xE0, 0x83, 0x9F, 0x81, 0x7A, 0xB1, 0xE5, 0xAD, 0x5B, 0xA0, 0xC4,
        0x1B, 0xC2, 0x38, 0x2F, 0xA2, 0x63, 0xC6, 0x4A, 0x9F, 0x25, 0x5F, 0x31, 0xF3, 0x5B, 0xC8, 0xDC,
        0x2B, 0xDF, 0x37, 0x7E, 0xC0, 0xBB, 0x1D, 0x41, 0x35, 0xCF, 0x27, 0xE2, 0x2A, 0xC8, 0xA7, 0x45,
        0x8A, 0xDF, 0x74, 0x2D, 0x43, 0x3B, 0x77, 0xE2, 0x61, 0xD7, 0x14, 0x54, 0x32, 0xDD, 0x30, 0x5E,
        0xBC, 0x68, 0xDC, 0x7D, 0x23, 0xF9, 0xBF, 0x59, 0xC8, 0xE6, 0x3C, 0xF3, 0xAF, 0xE3, 0x38, 0x39,
        0xC3, 0xAD, 0x67, 0x07, 0xE9, 0xDE, 0xEA, 0x4A, 0xB5, 0xFE, 0x9E, 0xE8, 0xDB, 0xCF, 0x46, 0x98,
        0x38, 0x22, 0x16, 0x5C, 0x1C, 0xD5, 0xB5, 0xE5, 0xBC, 0xF1, 0xD8, 0x6B, 0xD5, 0x11, 0xA5, 0x99,
        0xBE, 0x0C, 0xA0, 0xA7, 0xEB, 0x8B, 0xF2, 0xA1, 0x8A, 0x90, 0x89, 0x65, 0x60, 0xD0, 0xFE, 0x98,
        0xF2, 0x14, 0xF4, 0x0E, 0xEA, 0xAA, 0xF3, 0xA7, 0x9B, 0xCC, 0x13, 0x71, 0xDD, 0xB8, 0x0B, 0x9C,
        0xC5, 0xAB, 0x44, 0xB1, 0x37, 0x36, 0x52, 0xC1, 0xC7, 0xDC, 0x27, 0x27, 0xAC, 0x25, 0xC0, 0x9E,
        0x3A, 0xB4, 0xAD, 0x25, 0x5E, 0xA1, 0xCC, 0xFA, 0x6D, 0xE0, 0x6C, 0x4B, 0x99, 0x77, 0xE8, 0x5E,
    };

    private static final Formatter FORMATTER = new Formatter(Locale.US);

    public CrochetTimeChapter()
    {
    }

    private static int bigEndianInt(RandomAccessFile raf) throws IOException
    {
        int ret = 0;
        for(int i = 0; i < 4; i++)
        {
            ret += (raf.read()) << ((3 - i) * 8);
        }
        return(ret);
    }

    private static String getString(RandomAccessFile raf, int num) throws IOException
    {
        char[] c = new char[num];
        for(int i = 0; i < num; i++)
            c[i] = (char)raf.read();
        return(new String(c));
    }

    public static ArrayList<String> fileList(String fileName) throws IOException
    {
        ArrayList<String> ret = new ArrayList<String>();

        RandomAccessFile file = new RandomAccessFile(fileName, "r");

        file.seek(8);
        int offset = bigEndianInt(file);
        System.out.println("offset: " + offset);

        file.seek(offset);

        while(file.getFilePointer() < file.length())
        {
            String blockType;
            int blockSize;

            blockType = getString(file, 4);
            blockSize = bigEndianInt(file) - 8;

            if(blockType.equals("IMGI"))
            {
                int urlPart1 = 0, urlPart2 = 0, filenameSize = 0;

                file.skipBytes(0x2c);   // skip some unknown stuff
                urlPart2 = bigEndianInt(file);
                file.skipBytes(8);
                urlPart1 = bigEndianInt(file) + 0x10;

                filenameSize = blockSize - 0x98;
                file.skipBytes(0x5b);
                String fname = getString(file, filenameSize);

                FORMATTER.format("%s&D&%d&%d%08x", fname, urlPart1, urlPart2, (int)(32767 * Math.random()) + 0x4000);
                String result = FORMATTER.out().toString();
                ((StringBuilder)FORMATTER.out()).setLength(0);
                System.out.println("res: " + result);
                ret.add(result);
            }
            else
            {
                // other types, like thumbnails
            }
        }

        return(ret);
    }

    public static String scrambleURL(String src)
    {
        char[] str = src.toCharArray();

        int strHash = 0, idx, strLen, p1, p2;
        for(char c : str)
            strHash += c;

        idx = strHash;
        strLen = str.length;

        // scramble
        for(int i=0; i < strLen; i++)
        {
            int t = SCRAMBLE_KEY_TABLE[(idx+i)&0xff];
            t += i;

            p1 = t % strLen;
            t /= strLen;
            t = ((t|0xffffffff)-i)&0xff;

            t = SCRAMBLE_KEY_TABLE[t];
            t += i;
            p2 = t % strLen;


            if(p1 != p2) // same positions, don't swap bytes
            {
                char c = str[p1];
                str[p1] = str[p2];
                str[p2] = c;
            }
        }

        // replace
	for(int i=0; i < strLen; i++)
	{
            if((str[i]-0x61) <= 0x19 && (str[i]-0x61) >= 0) // lower case
            {
                int t = SCRAMBLE_KEY_TABLE[strLen+i];
                t += str[i]-0x61;

                str[i] = (char)((t % 0x1a) + 0x61);
            }
            else if((str[i]-0x41) <= 0x19 && (str[i]-0x41) >= 0) // upper case
            {
                int t = SCRAMBLE_KEY_TABLE[strLen+i];
                t += str[i]-0x41;

                str[i] = (char)((t % 0x1a) + 0x41);
            }
            else if((str[i]-0x30) <= 0x9 && (str[i]-0x30) >= 0) // number
            {
                int t = SCRAMBLE_KEY_TABLE[strLen+i];
                t += str[i]-0x30;

                str[i] = (char)((t % 0x0a) + 0x30);
            }
	}

        String ret = null;
        try
        {
            ret = java.net.URLEncoder.encode(new String(str), "UTF-8");
        }
        catch(java.io.UnsupportedEncodingException uee)
        {
            DownloaderUtils.error("UTF-8 not found", uee, true);
        }

        return(ret);
    }
}
