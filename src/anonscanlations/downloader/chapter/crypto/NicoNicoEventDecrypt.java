package anonscanlations.downloader.chapter.crypto;

import java.util.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class NicoNicoEventDecrypt
{
    public static void decrypt(byte[] bytes) throws Exception
    {
        java.security.MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
        long _loc_4 = 0;
        md5.reset();
        md5.update(("" + bytes.length).getBytes());
        String _loc_2 = "";
        for(byte b : md5.digest())
        {
            _loc_2 += String.format("%02x", b);
        }
        ArrayList<Integer> _loc_3 = new ArrayList<Integer>();
        while(_loc_4 < 8)
        {
            _loc_3.add(Integer.parseInt(_loc_2.substring((int)(_loc_4 * 2), (int)(_loc_4 * 2) + 2), 16));
            _loc_4 = _loc_4 + 1;
        }
        int _loc_5 = _loc_3.size();
        _loc_4 = 0;
        while(_loc_4 < bytes.length)
        {
            bytes[(int)_loc_4] = (byte)(bytes[(int)_loc_4] ^ _loc_3.get((int)(_loc_4 % _loc_5)));
            _loc_4 = _loc_4 + 1;
        }
    }
}
