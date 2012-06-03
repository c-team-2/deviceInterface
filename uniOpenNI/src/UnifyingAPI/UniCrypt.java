package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * Dummy class to be used for encrypting and decrypting sensor packets;
 * @author Greg Clark
 *
 */
public class UniCrypt {
	
	public ByteBuffer decrypt(ByteBuffer encryptedBuffer)
	{
		return encryptedBuffer;
	}
	
	public ByteBuffer encrypt(ByteBuffer rawBuffer)
	{
		return rawBuffer;
	}

}
