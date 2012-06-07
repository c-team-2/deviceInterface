package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * A channel of raw data from the device. Used for driver-side creation and 
 * middleware-side parsing of channel packets.
 * @author Greg Clark
 *
 */
public class UniChannel {
	
	private UniDataPacker packer;
	private ByteBuffer data;
	private UniChannelHeader header;
	
	/**
	 * Construct a <code>UniChannel</code> from a sensor packet.
	 * @param sensorPacket the <code>ByteBuffer</code> containing the sensor packet, 
	 * with its position where the channel header starts
	 */
	public UniChannel(ByteBuffer sensorPacket) 
	{		
		header = new UniChannelHeader(sensorPacket);
		
		int dataSize = (int) getPackedDataSize();
		ByteBuffer dataBuffer = sensorPacket.slice();
		
		data = (ByteBuffer) dataBuffer.limit(dataSize);
	}


	public UniChannel(UniChannelHeader header,
			UniDataPacker dataPacker) {
		this.header = header;
		this.packer = dataPacker;
	}

	/**
	 * Write the channel packet into a <code>ByteBuffer</code> at the 
	 * <code>ByteBuffer</code>'s position.
	 * @param sensorPacket the <code>ByteBuffer</code> to write the channel packet into, 
	 * positioned where the channel packet should start.
	 */
	public final void packIntoByteBuffer(ByteBuffer sensorPacket) 
	{
		header.packIntoByteBuffer(sensorPacket);		
		packer.writeDataIntoByteBuffer(sensorPacket.slice());
	}
	
	/**
	 * Retrieves the size in bytes that this channel requires when written in 
	 * the channel packet format.
	 * @return number of bytes needed to store this channel in a channel packet
	 */
	public long getPackedSize() 
	{
		long dataSize = getPackedDataSize();
		
		// Add header size
		int headerSize = header.getPackedSize();
		long channelSize = dataSize + headerSize;
		
		return channelSize;
	}
	
	/**
	 * Returns the size of the data in bytes. Calculated from the <code>UniElementDescriptor</code>s
	 * in this channel's <code>UniChannelHeader</code>.
	 * @return size of the data in bytes
	 */
	public long getPackedDataSize()
	{
		// Construct size (in bits) of a tuple
		// assumes that elements are byte aligned
		UniElementDescriptor descriptors[] = header.getElementDescriptors();
		long tupleSize = 0;	// Sized in bytes
		for (int i = 0; i < descriptors.length; ++i) 
		{
			int elementSizeInBits = descriptors[i].getSize() * (1 << (descriptors[i].isSizedInBytes()?3:0));
			int elementSizeInBytes = elementSizeInBits >> 3;
			
			// Add a byte if doesn't end on byte boundary
			elementSizeInBytes += ((elementSizeInBits % 8) > 0 ? 1 : 0);
			tupleSize+= elementSizeInBytes;
		}
		
		// Construct buffer size (in bytes)
		long dataSize = tupleSize * header.getNumberTuples();
		
		return dataSize;
	}
	
	/**
	 * Retrieves the channel's data buffer.
	 * @return the channel's data buffer
	 */
	public ByteBuffer getData() { return data; }
	
	/**
	 * Retrieves the channel's header.
	 * @return the channel's header
	 */
	public UniChannelHeader getHeader() { return header; }
}
