/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.chapter.crypto;

import java.io.*;

/**
 *
 * @author Nagato and /a/non
 */
public class PCViewerDecrypt
{
    public static void decrypt(byte[] data)
    {
        for(int i = 0; i < data.length - 4; i++)
        {
            if(i == (data.length - 5))
                data[i] = (byte)(data[i] ^ (data[i + 1]
                                        ^ data[i + 2]
                                        ^ data[i + 3]
                                        ^ data[i + 4]));
            else
                data[i] = (byte)(data[i] ^ data[i + 1]);
        }
    }

    public static void encrypt(byte[] data)
    {
        data[data.length - 5] = (byte)(data[data.length - 5]
                                        ^ (data[data.length - 4]
                                            ^ data[data.length - 3]
                                            ^ data[data.length - 2]
                                            ^ data[data.length - 1]));
        for(int i = data.length - 6; i >= 0; i--)
        {
            data[i] = (byte)(data[i] ^ data[i + 1]);
        }
    }
}
