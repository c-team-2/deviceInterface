package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * Represents the header of a channel packet.
 * @author Greg Clark
 *
 */
public class UniChannelHeader {
	private long numTuples;
	private double frequency;
	private short elementsPerTuple;
	private UniElementDescriptor elementDescriptors[];
	private String name;
	
	public long getNumberTuples() { return numTuples; }
	public double getFrequency() { return frequency; }
	public UniElementDescriptor[] getElementDescriptors() { return elementDescriptors; }
	public String getName() { return name; }
	
	/**
	 * Construct a <code>UniChannelHeader</code> from a sensor packet.
	 * @param sensorPacket the <code>ByteBuffer</code> containing the sensor packet, 
	 * with its position where the channel header starts
	 */
	public UniChannelHeader(ByteBuffer sensorPacket) 
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
	
	public UniChannelHeader(long numTuples, double frequency,
			UniElementDescriptor elementDescriptors[], String name)
	{
		this.numTuples = numTuples;
		this.frequency = frequency;
		this.elementsPerTuple = (short) elementDescriptors.length;
		this.elementDescriptors = elementDescriptors;
		this.name = name;
	}
	
	/**
	 * Write the channel packet header into a <code>ByteBuffer</code> at the <code>ByteBuffer</code>'s position.
	 * @param sensorPacket the <code>ByteBuffer</code> to write the channel packet into, 
	 * positioned where the channel packet should start.
	 */
	public boolean packIntoByteBuffer(ByteBuffer sensorPacket)
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
	 * Returns the size in bytes that this channel header would require if written in 
	 * the channel packet format.
	 * @return number of bytes needed to store this channel header in a channel packet
	 */
	public int getPackedSize()
	{
		int bytes = 18; 	// Add size of timestamp, frequency, elementsPerTuple
		bytes += elementDescriptors.length; // Add size of element descriptors
		bytes += (name.length() + 1) << 1; // Add size of name
		
		return bytes;
	}

}
