package anonscanlations.downloader.chapter.crypto;

import java.io.*;
import java.security.*;

import anonscanlations.downloader.extern.*;

/** Decryption functions for NicoNico E-Books
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoEBooksDecrypt
{
    private static final int FIXED_LOCAL_FILE_HEADER_LENGTH = 30,
                            FILENAME_LENGTH_POSITION = 26,
                            EXTRAFIELD_LENGTH_POSITION = 28;
    private static final String SEED = "ojtDYr93p-h-9yt-ghOUG08fwigap1u0fnV";

    public static byte[] createKey(byte[] array, byte[] userBytes, byte[] bookBytes) throws Exception
    {
        int start = ((int)array[FILENAME_LENGTH_POSITION + 1] << 8) +
                            (int)array[FILENAME_LENGTH_POSITION];

        int length = ((int)array[EXTRAFIELD_LENGTH_POSITION + 1] << 8) +
                        (int)array[EXTRAFIELD_LENGTH_POSITION];

        byte[] salt = new byte[length];
        for(int i = 0; i < length; i++)
            salt[i] = array[FIXED_LOCAL_FILE_HEADER_LENGTH + start + i];

        int _loc_5 = salt[0] & 0xff;
        int userIdIndex = (_loc_5 & 192) >> 6;
        int identifierBookIdIndex = (_loc_5 & 48) >> 4;
        int seedIndex = (_loc_5 & 12) >> 2;
        int saltIndex = _loc_5 & 3;

        byte[][] _loc_4 = new byte[4][];
        _loc_4[userIdIndex] = userBytes; //userid.getBytes("UTF-8");
        _loc_4[identifierBookIdIndex] = bookBytes; //bookid.getBytes("UTF-8");
        _loc_4[seedIndex] = SEED.getBytes("UTF-8");
        _loc_4[saltIndex] = salt;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for(byte[] ba : _loc_4)
            bos.write(ba);
        byte[] bytes = bos.toByteArray();
        bos.close();

        return(MessageDigest.getInstance("MD5").digest(bytes));
    }

    public static byte[] decrypt(byte[] bytes, byte[] key)
    {
        ARC4 arc4 = new ARC4(key);
        return(arc4.arc4Crypt(bytes));
    }
}
