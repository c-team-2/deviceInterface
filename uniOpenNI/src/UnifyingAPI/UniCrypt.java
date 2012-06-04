package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * Dummy class to be used for encrypting and decrypting sensor packets;
 * @author Greg Clark
 *
 */
public class UniCrypt {
	
	public ByteBuffer encrypt(int encryptionFlags, ByteBuffer rawBuffer)
	{
		return rawBuffer;
	}

	public ByteBuffer decrypt(int encryptionFlags, ByteBuffer encryptedBuffer) {
		return encryptedBuffer;
	}

}
