import java.nio.ByteBuffer;


public class UniChannelHeader {
	private long numTuples;
	private double frequency;
	private short elementsPerTuple;
	private UniElementDescriptor elementDescriptors[];
	private String name;
	
	long getNumberTuples() { return numTuples; }
	double getFrequency() { return frequency; }
	UniElementDescriptor[] getElementDescriptors() { return elementDescriptors; }
	String getName() { return name; }
	
	UniChannelHeader(ByteBuffer sensorPacket) 
	{
		numTuples = sensorPacket.getLong();
		frequency = sensorPacket.getDouble();
		elementsPerTuple = sensorPacket.getShort();
		
		// Get element descriptors
		elementDescriptors = new UniElementDescriptor[elementsPerTuple];
		for(int i = 0; i < elementsPerTuple; ++i) {
			elementDescriptors[i] = new UniElementDescriptor(sensorPacket.get());
		}
		
		// Get name
		StringBuffer buffer = new StringBuffer();
		char c;
		while ((c = sensorPacket.getChar()) != '\u0000')
		{
			buffer.append(c);
		}
		name = buffer.toString();
	}
	
	UniChannelHeader(long numTuples, double frequency,
			UniElementDescriptor elementDescriptors[], String name)
	{
		this.numTuples = numTuples;
		this.frequency = frequency;
		this.elementsPerTuple = (short) elementDescriptors.length;
		this.elementDescriptors = elementDescriptors;
		this.name = name;
	}
	
	boolean packIntoByteBuffer(ByteBuffer sensorPacket)
	{
		sensorPacket.putLong(numTuples);
		sensorPacket.putDouble(frequency);
		sensorPacket.putShort(elementsPerTuple);
		for(int i = 0; i < elementsPerTuple; ++i) {
			sensorPacket.put(elementDescriptors[i].getDescriptor());
		}
		
		// Pack name
		for (int i = 0; i < name.length(); ++i)
		{
			sensorPacket.putChar(name.charAt(i));
		}
		sensorPacket.putChar('\u0000');
		
		return true;
	}
	
	/**
	 * 
	 * @return the number of bytes needed for this header in a channel packet
	 */
	int getPackedSize()
	{
		int bytes = 18; 	// Add size of timestamp, frequency, elementsPerTuple
		bytes += elementDescriptors.length; // Add size of element descriptors
		bytes += (name.length() + 1) << 1; // Add size of name
		
		return bytes;
	}

}
