import java.nio.ByteBuffer;


public class UniChannel {
	private ByteBuffer data;
	private UniChannelHeader header;
	
	public UniChannel(ByteBuffer sensorPacket) {		
		header = new UniChannelHeader(sensorPacket);
		data = sensorPacket.slice().asReadOnlyBuffer();
	}
	
	public UniChannel(UniChannelHeader header, ByteBuffer data) {		
		this.header = header;
		this.data = data;
	}

	boolean packIntoByteBuffer(ByteBuffer sensorPacket) 
	{
		header.packIntoByteBuffer(sensorPacket);
		sensorPacket.put(data);
		return true;
	}
	
	long getPacketSize() 
	{
		// Construct size (in bits) of a tuple
		// TODO: assumes that tuples and channels are byte-aligned, not necessarily elements
		UniElementDescriptor descriptors[] = header.getElementDescriptors();
		long tupleSizeInBits = 0;
		for (int i = 0; i < descriptors.length; ++i) 
		{
			int elementSizeInBits = descriptors[i].getSize() * (descriptors[i].isSizedInBytes()?1:0 << 3);
			tupleSizeInBits += elementSizeInBits;
		}
		
		// Construct buffer size (in bytes)
		long channelSizeInBits = tupleSizeInBits * header.getNumberTuples();
		long channelSize = channelSizeInBits >> 3;
		channelSize += ((channelSizeInBits & 7) > 0 ? 1 : 0); // Add an extra byte if data doesn't end on byte boundary
		channelSize += 18; 	// Add size of timestamp, frequency, elementsPerTuple
		channelSize += descriptors.length; // Add size of element descriptors
		channelSize += ((header.getName().length() + 1) << 1); // Add size of name
		
		return channelSize;
	}
	
	ByteBuffer getData() { return data; }
	UniChannelHeader getHeader() { return header; }
}
