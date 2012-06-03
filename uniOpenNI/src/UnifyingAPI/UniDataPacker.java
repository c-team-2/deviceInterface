package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * Interface for writing channel data into a channel packet.
 * @author Greg Clark
 *
 */
public interface UniDataPacker
{
	/**
	 * Writes the channel data into the given buffer starting at the buffer's 
	 * position. This is to be implemented by device driver developers. The data 
	 * should be Big Endian.
	 * @param buffer the buffer to write into.
	 */
	public void writeDataIntoByteBuffer(ByteBuffer buffer);
}
