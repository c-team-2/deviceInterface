import java.nio.ByteBuffer;

/**
 * A channel of raw data from the device.
 * @author Greg Clark
 *
 */
public class UniChannel {
	private ByteBuffer data;
	private UniChannelHeader header;
	
	/**
	 * Construct a UniChannel from a sensor packet.
	 * @param sensorPacket - the ByteBuffer containing the sensor packet, 
	 * with its position where the channel header starts
	 */
	public UniChannel(ByteBuffer sensorPacket) {		
		header = new UniChannelHeader(sensorPacket);
		
		// Add padding before reading data
		int padding = (8 - (sensorPacket.position() % 8)) % 8;
		sensorPacket.position(sensorPacket.position() + padding);
		
		int dataSize = (int) getPackedDataSize();
		ByteBuffer dataBuffer = sensorPacket.slice();
		
		data = (ByteBuffer) dataBuffer.limit(dataSize);
	}
	
	public UniChannel(UniChannelHeader header, ByteBuffer data) {		
		this.header = header;
		this.data = data;
	}

	/**
	 * Write the channel packet into a ByteBuffer at the ByteBuffer's position.
	 * @param sensorPacket - the ByteBuffer to write the channel packet into, 
	 * positioned where the channel packet should start.
	 */
	void packIntoByteBuffer(ByteBuffer sensorPacket) 
	{
		header.packIntoByteBuffer(sensorPacket);
		
		// Add padding before packing data
		int padding = (8 - (sensorPacket.position() % 8)) % 8;
		sensorPacket.position(sensorPacket.position() + padding);
				
		sensorPacket.put(data);
	}
	
	/**
	 * Returns the size in bytes that this channel would require if written in 
	 * the channel packet format.
	 * @return number of bytes needed to store this channel in a channel packet
	 */
	long getPackedSize() 
	{
		long dataSize = getPackedDataSize();
		
		// Add header size
		int headerSize = header.getPackedSize();
		long channelSize = dataSize + headerSize;
		
		return channelSize;
	}
	
	/**
	 * Returns the size of the data in bytes. Calculated from the UniElementDescriptors
	 * in this channel's header.
	 * @return size of the data in bytes
	 */
	long getPackedDataSize()
	{
		// Construct size (in bits) of a tuple
		// TODO: assumes that tuples and channels are byte-aligned, not necessarily elements
		UniElementDescriptor descriptors[] = header.getElementDescriptors();
		long tupleSizeInBits = 0;
		for (int i = 0; i < descriptors.length; ++i) 
		{
			int elementSizeInBits = descriptors[i].getSize() * (1 << (descriptors[i].isSizedInBytes()?3:0));
			tupleSizeInBits += elementSizeInBits;
		}
		
		// Construct buffer size (in bytes)
		long dataSizeInBits = tupleSizeInBits * header.getNumberTuples();
		long dataSize = dataSizeInBits >> 3;
		dataSize += ((dataSizeInBits & 7) > 0 ? 1 : 0); // Add an extra byte if data doesn't end on byte boundary
		
		// Add padding to 64-bit word boundary
		long padding = (8 - (dataSize % 8)) % 8;
		dataSize += padding;
		
		return dataSize;
	}
	
	ByteBuffer getData() { return data; }
	UniChannelHeader getHeader() { return header; }
}
