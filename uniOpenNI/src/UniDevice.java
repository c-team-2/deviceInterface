import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Represents a device that implements the Unified API.
 * @author Greg Clark
 *
 */
public abstract class UniDevice {
	
	UniDevice()
	{
		this.encryptor = new UniCrypt();
		this.channels = new LinkedList<UniChannel>();
	}
	
	/**
	 * Constructs and returns a new sensor packet as a byte array.
	 * @return the sensor packet
	 */
	public final byte[] getSensorPacket()
	{
		// Create a new channels list
		channels = new LinkedList<UniChannel>();
		
		// Have the driver update the channel data
		updateChannels();
		
		// Create a new sensor header
		short numChannels = (short) channels.size();
		UniSensorHeader sensorHeader = new UniSensorHeader((byte)1, vendorID, 
				productID, numChannels, System.currentTimeMillis(), frequency, encryptionFlags);
		
		// Determine size of bytebuffer to allocate
		int sensorHeaderSize = sensorHeader.getPackedSize();
		int channelSize = 0;
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			channelSize += channels.get(channelCount).getPackedSize();
		}
		int sensorPacketSize = sensorHeaderSize + channelSize;
		
		// Allocate the ByteBuffer
		ByteBuffer buffer = ByteBuffer.allocateDirect(sensorPacketSize);
		
		// Pack the sensor packet into the buffer
		buffer.rewind();
		sensorHeader.packIntoByteBuffer(buffer);
		int packedSize = sensorHeader.getPackedSize();
		
		// Pack the channels into the buffer
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			UniChannel channel = channels.get(channelCount);
			buffer.position(packedSize);
			channel.packIntoByteBuffer(buffer);
			packedSize += channel.getPackedSize();
		}
		
		// Encrypt the buffer
		buffer.rewind();
		ByteBuffer encryptedBuffer = encryptor.encrypt(buffer);
		
		// Create and return the byte array
		byte[] sensorPacket = new byte[encryptedBuffer.capacity()];
		encryptedBuffer.get(sensorPacket);
		return sensorPacket;
	}
	
	/**
	 * Add a UniChannel to the channels list.
	 * @param channel the UniChannel to add to the list
	 */
	protected final void addChannel(UniChannel channel)
	{
		channels.add(channel);
	}
	
	/**
	 * The only function that is necessary for the driver implementors to implement.
	 * Adds each channel to the channels list using addChannel()
	 */
	protected abstract void updateChannels();
	
	private UniCrypt encryptor;
	private LinkedList<UniChannel> channels;
	private short vendorID;
	private short productID;
	private int encryptionFlags;
	private double frequency;
}
