/*
 * RC4 implementation
 * Version 1.0.1
 * Released in to the public domain
 * Written by: Kyle Givler
 *
 *
 * $Id: ARC4.java,v 1.9 2010/12/02 21:55:54 kwgivler Exp $
 * $Log: ARC4.java,v $
 * Revision 1.9  2010/12/02 21:55:54  kwgivler
 * Minor fixes
 *
 * Revision 1.8  2010/12/02 21:11:55  kwgivler
 * Updating documentation/typos and other minor revisions
 *
 * Revision 1.7  2010/11/20 20:41:30  kwgivler
 * Ready for Initial Release
 *
 */

package anonscanlations.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * This is a simple example implementation of the (Alleged) RC4 stream chiper algorithm.
 * You probably shouldn't this for anything serious though, RC4 has some flaws,
 * plus I don't guarantee the implementation is correct.
 *
 * It was written mostly just for fun :)
 *
 * @author Kyle Givler
 */

public class ARC4 {

	private byte state[] = new byte[256]; // The "state"
	private int i; // index pointer 'i'
	private int j; // index pointer 'j'
	private boolean isReady; // state initialized?


	/**
	 * Default Constructor.
	 *
	 * Mostly useful just for key generation.
	 * You must manually call arc4Init before using any method
	 * other than generateKey()
	 */
	public ARC4()
	{
		isReady = false;
	}

	/**
	 * Constructs the ARC4 object using the given "password" (String key).
	 * @param key Key to initialize object
	 */
	public ARC4(String key)
	{
		arc4Init(key.getBytes());
	}

	/**
	 * Full Constructor.
	 *
	 * Constructs the ARC4 object using the given key.
	 * @param key Key must be between 40 and 2048 bits [5 - 256 bytes]
	 */
	public ARC4(byte[] key)
	{
		arc4Init(key);
	}



	/**
	 * The key-scheduling algorithm (KSA).
	 * @param key "password" (String key) to use to initialize KSA
	 */
	public void arc4Init(String key)
	{
		arc4Init(key.getBytes());
	}

	/**
	 * The key-scheduling algorithm (KSA).
	 * Initializes the state
	 * @param key must be between 5 and 256 bytes
	 */
	public void arc4Init(byte[] key)
	{
		if (key == null || key.length < 5 || key.length > 256)
			throw new IllegalArgumentException("Invalid key! Key length must be between 5 and 256 bytes");

		i = 0;
		j = 0;

		for (int i = 0; i < 256; i++)
			state[i] = (byte) i;

		for (int i = 0; i < 256; i++)
			{
				j = (j + state[i] + key[i % key.length]) & 255;
				swap(i, j);
			}
		j = 0;
		i = 0;
		isReady = true;
	}



	/**
	 * The pseudo-random generation algorithm (PRGA)
	 * @return the next byte in the keystream
	 */
	public byte arc4PRGA()
	{
		if (!isReady)
		{
			throw new IllegalStateException("Must Call arc4Init() first!");
		}
		i = (i + 1) & 255;
		j = (j + state[i]) & 255;
		swap(i,j);

		// K (keystream) = state[(state[i] + state[j]) & 255]
		return state[(state[i] + state[j]) & 255];
	}



	/**
	 * RC4 en/decryption
	 * @param data data to en/decrypt
	 * @return result of en/decryption
	 */
	public byte[] arc4Crypt (String data)
	{
		return arc4Crypt(data.getBytes());
	}

	/**
	 * RC4 encryption/deccyption
	 * @param buffer the data to be en/decrypted
	 * @return result of en/decryption
	 */
	public byte[] arc4Crypt(byte[] buffer)
	{
		byte[] result = new byte[buffer.length];
		for (int x = 0; x < buffer.length; x++)
		{
			result[x] = arc4Crypt(buffer[x]);
		}
		return result;
	}

	/**
	 * RC4 encryption/deccyption
	 * @param data byte to en/decrypt
	 * @return en/decrypted byte
	 */
	public byte arc4Crypt(byte data)
	{
		byte k = arc4PRGA();
		return (byte) (data ^ k);
	}


	/**
	 * Encrypt a String
	 * @param data to encrypt
	 * @return byte[] array containing encrypted string
	 */
	public byte[] encrypt(String data)
	{
		byte result[] = arc4Crypt(data.getBytes());
		isReady = false;
		return result;
	}

	/**
	 * Decrypt to a string
	 * @param data byte[] array containing encrypted string
	 * @return decrypted string
	 */
	public String decrypt(byte data[])
	{
		byte temp[] = arc4Crypt(data);
		isReady = false;
		return new String(temp);
	}

	/**
	 * Generate a RC4 key (using KeyGenerator, 128 byte key limit)
	 * @param length Length of the key to be generated in bytes [range: 5-128]
	 * @throws NoSuchAlgorithmException if RC4 not supported
	 */
	public byte[] generateKey(int length) throws NoSuchAlgorithmException
	{
		if (length < 5 || length > 128)
		{
			throw new IllegalArgumentException("Key must be between 5 and 128 bytes");
		}

		SecretKey key;
		KeyGenerator keyGen = KeyGenerator.getInstance("RC4");

		keyGen.init(length * 8); // length in bits * bits in a byte
		key = keyGen.generateKey();

		return key.getEncoded();
	}

	/**
	 * En/decrypt a file
	 * @param in File to en/decrypt
	 * @param out output File
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void encFile(File in, File out) throws FileNotFoundException, IOException
	{
		InputStream inStream = new FileInputStream(in); // file to en/decrypt
		OutputStream outStream = new FileOutputStream(out); // output file
		long length = in.length();

        // En/decrypt File
        for (int i = 0; i < length; i++)
        {
        	byte inByte;
        	inByte = (byte) inStream.read();
        	byte OutByte = arc4Crypt(inByte);

        	outStream.write(OutByte);
        }
        inStream.close();
        outStream.close();
        isReady = false;
	}

	/**
	 * swaps a and b in state[]
	 * @param a 1st index
	 * @param b 2nd index
	 */
	private void swap(int a, int b)
	{
		byte temp = state[a];
		state[a] = state[b];
		state[b] = temp;
	}
}