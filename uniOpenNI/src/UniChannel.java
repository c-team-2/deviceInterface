import java.nio.ByteBuffer;


public class UniChannel {
	private ByteBuffer data;
	private UniChannelHeader header;
	
	public UniChannel(ByteBuffer sensorPacket) {		
		header = new UniChannelHeader(sensorPacket);
		
		// Add padding before reading data
		int padding = (8 - (sensorPacket.position() % 8)) % 8;
		sensorPacket.position(sensorPacket.position() + padding);
		
		int dataSize = (int) getPackedDataSize();
		ByteBuffer dataBuffer = sensorPacket.slice();
		
		//TODO: not sure if this casting is okay
		data = (ByteBuffer) dataBuffer.limit(dataSize);
	}
	
	public UniChannel(UniChannelHeader header, ByteBuffer data) {		
		this.header = header;
		this.data = data;
	}

	boolean packIntoByteBuffer(ByteBuffer sensorPacket) 
	{
		header.packIntoByteBuffer(sensorPacket);
		
		// Add padding before packing data
		int padding = (8 - (sensorPacket.position() % 8)) % 8;
		sensorPacket.position(sensorPacket.position() + padding);
				
		sensorPacket.put(data);
		return true;
	}
	
	/**
	 * 
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
