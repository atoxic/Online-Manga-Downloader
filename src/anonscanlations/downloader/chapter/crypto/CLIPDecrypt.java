package anonscanlations.downloader.chapter.crypto;

/**
 *
 * @author /a/non
 */
public class CLIPDecrypt
{
    public static String createHash(byte[] param1)
    {
        String _loc_2 = null;
        int _loc_4 = 0;
        int _loc_5 = 0;
        int[] _loc_3 = new int[6];
        _loc_4 = 0;
        while (_loc_4 < 6)
        {
            _loc_3[_loc_4] = 0;
            _loc_4++;
        }
        int _loc_6 = param1.length;
        _loc_5 = 0;
        while (_loc_5 < _loc_6)
        {
            _loc_3[0] = _loc_3[0] + param1[_loc_5];
            _loc_3[1] = param1[_loc_5] - _loc_3[1];
            _loc_3[2] = _loc_3[2] ^ param1[_loc_5];
            _loc_5 = _loc_5 + 10;
        }
        _loc_3[3] = _loc_6 >> 16;
        _loc_3[4] = _loc_6 >> 8;
        _loc_3[5] = _loc_6;
        _loc_4 = 0;
        while (_loc_4 < 6)
        {
            _loc_3[_loc_4] = _loc_3[_loc_4] & 255;
            _loc_4++;
        }
        int _loc_7 = (_loc_3[3] << 16) + (_loc_3[4] << 8) + _loc_3[5];
        _loc_2 = Integer.toString((_loc_3[3] << 16) + (_loc_3[4] << 8) + _loc_3[5], 16);
        while(_loc_2.length() < 6)
        {
            _loc_2 = "0" + _loc_2;
        }
        _loc_7 = (_loc_3[0] << 16) + (_loc_3[1] << 8) + _loc_3[2];
        _loc_2 = Integer.toString(_loc_7, 16) + _loc_2;
        while(_loc_2.length() < 12)
        {
            _loc_2 = "0" + _loc_2;
        }
        return _loc_2;
    }
    public static int createRequestID(byte[] time)
    {
        return((((int)time[0] & 0xff) << 24) |
                (((int)time[1] & 0xff) << 16) | 
                (((int)time[2] & 0xff) << 8) |
                (time[3] & 0xff));
    }
    
    public static byte[] decodeKey(byte[] key, byte[] time)
    {
        byte[] decodeKey = new byte[8];
        decodeKey[0] = (byte)(key[0] ^ time[4]);
        decodeKey[1] = (byte)(key[1] ^ time[5]);
        decodeKey[2] = (byte)(key[2] ^ time[6]);
        decodeKey[3] = (byte)(key[3] ^ time[7]);
        decodeKey[4] = (byte)(key[4] ^ time[4]);
        decodeKey[5] = (byte)(key[5] ^ time[5]);
        decodeKey[6] = (byte)(key[6] ^ time[6]);
        decodeKey[7] = (byte)(key[7] ^ time[7]);
        return(decodeKey);
    }
    
    public static byte[] decodeBinary(byte[] data, byte[] key)
    {
        boolean _loc_6 = false;
        int _loc_7 = 0;
        byte[] _loc_3 = new byte[key.length];
        System.arraycopy(key, 0, _loc_3, 0, key.length);
        int _loc_4 = 0;
        int _loc_5 = _loc_3[0] & 0xff;
        _loc_6 = false;
        while (!_loc_6)
        {
            _loc_7 = 0;
            while (_loc_7 < _loc_3.length)
            {
                if (!_loc_6)
                {
                    data[_loc_5] = (byte)(data[_loc_5] ^ _loc_3[_loc_7]);
                    _loc_4 = data[_loc_5] & 0xff;
                    if (_loc_4 == 0)
                    {
                        _loc_4++;
                    }
                    _loc_3[_loc_7] = (byte)_loc_4;
                    _loc_5 = _loc_5 + _loc_4;
                    if (_loc_5 >= data.length)
                    {
                        _loc_6 = true;
                    }
                }
                _loc_7++;
            }
        }
        return(data);
    }
}
