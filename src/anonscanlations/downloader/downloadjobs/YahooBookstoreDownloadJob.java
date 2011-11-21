package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.net.*;
import java.util.zip.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author /a/non <anonymousscanlations@gmail.com>
 */
public class YahooBookstoreDownloadJob extends ByteArrayDownloadJob
{
    //final byte[] publicIV = Base64.decode("8nrcUKAHo7latHeMq3k/Bg==");
    //final byte[] publicKey = Base64.decode("9iA9KscKT7bdRHNDeblXqA==");
    public static final byte[] publicIV =
                                    new byte[]{(byte)0xf2, (byte)0x7a, (byte)0xdc, (byte)0x50,
                                                (byte)0xa0, (byte)0x07, (byte)0xa3, (byte)0xb9, 
                                                (byte)0x5a, (byte)0xb4, (byte)0x77, (byte)0x8c, 
                                                (byte)0xab, (byte)0x79, (byte)0x3f, (byte)0x06},
                    publicKey = new byte[]{(byte)0xf6, (byte)0x20, (byte)0x3d, (byte)0x2a,
                                            (byte)0xc7, (byte)0x0a, (byte)0x4f, (byte)0xb6, 
                                            (byte)0xdd, (byte)0x44, (byte)0x73, (byte)0x43, 
                                            (byte)0x79, (byte)0xb9, (byte)0x57, (byte)0xa8};
    // TODO: add support for user keys

    private File file;

    public YahooBookstoreDownloadJob(String _desc, URL _url, File _file)
    {
        super(_desc, _url);
        file = _file;
    }

    @Override
    public void run() throws Exception
    {
        super.run();

        SecretKeySpec k = new SecretKeySpec(publicKey, "AES");
        int i1 = 0;
        int i2 = bytes.length;
        int i4 = 0;
        byte[] bArr1 = new byte[publicIV.length];
        System.arraycopy(publicIV, 0, bArr1, 0, publicIV.length);
        ByteArrayOutputStream memoryStream = new ByteArrayOutputStream();

        while(i2 > 0)
        {
            Cipher c = Cipher.getInstance("AES/CFB128/NoPadding");
            if(i2 > 16)
                i4 = 16;
            else
                i4 = i2;
            c.init(Cipher.DECRYPT_MODE, k, new IvParameterSpec(bArr1));
            memoryStream.write(c.doFinal(bytes, i1, i4));

            i1 += 16;
            i2 -= 16;

            bArr1[15] = (byte)(bArr1[15] + 1);
            if(bArr1[15] == 0)
            {
                bArr1[14] = (byte)(bArr1[14] + 1);
                if(bArr1[14] == 0)
                {
                    bArr1[13] = (byte)(bArr1[13] + 1);
                    if(bArr1[13] == 0)
                    {
                        bArr1[12] = (byte)(bArr1[12] + 1);
                        if(bArr1[12] == 0)
                        {
                            bArr1[11] = (byte)(bArr1[11] + 1);
                            if(bArr1[11] == 0)
                            {
                                bArr1[10] = (byte)(bArr1[10] + 1);
                                if(bArr1[10] == 0)
                                    bArr1[9] = (byte)(bArr1[9] + 1);
                            }
                        }
                    }
                }
            }
        }
        memoryStream.close();

        byte[] decrypted = memoryStream.toByteArray();
        int i5 = decrypted[decrypted.length - 1] & 0xff;
        byte[] decrypted2 = new byte[decrypted.length - i5];
        System.arraycopy(decrypted, 0, decrypted2, 0, decrypted2.length);

        InputStream inflater = new ByteArrayInputStream(decrypted2);
        OutputStream fos = new InflaterOutputStream(new FileOutputStream(file), new Inflater(true));
        byte[] buf = new byte[1024];
        while(inflater.read(buf) != -1)
            fos.write(buf);
        inflater.close();
        fos.close();
    }
}
