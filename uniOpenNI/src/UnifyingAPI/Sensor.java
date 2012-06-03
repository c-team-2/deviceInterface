package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * The interface used to retrieve and parse sensor packets from a <code>UniDevice</code>. 
 * @author Greg Clark
 *
 */
public class Sensor {
	
	/**
	 * Dummy function to be implemented later for decrypting the sensor packet
	 * @param encryptedBuffer
	 * @param encryptionFlags
	 * @return
	 */
	private ByteBuffer decrypt(int encryptionFlags, ByteBuffer encryptedBuffer)
	{
		return encryptedBuffer;
	}
	
	private UniDevice device;
	
	public Sensor(UniDevice device)
	{
		this.device = device;
	}
	
	/**
	 * Asks the device to construct a new sensor packet and returns the data as
	 *  a <code>SensorSnapshot</code>. This method will block while waiting for
	 *  the device to return a sensor packet.
	 * @return the <code>SensorSnapshot</code>
	 */
	public SensorSnapshot getSensorSnapshot()
	{
		// Get sensor packet from device driver
		ByteBuffer rawPacket = ByteBuffer.wrap(device.getSensorPacket()).asReadOnlyBuffer();
		rawPacket.rewind();
		
		// Create new SensorSnapshot
		UniSensorHeader sensorHeader = new UniSensorHeader(rawPacket); 
		SensorSnapshot snapshot = new SensorSnapshot(this, sensorHeader);
		
		// Parse sensor packet into Channels and add to SensorSnapshot
		int numChannels = sensorHeader.getNumChannels();
		
		// Decrypt the raw packet
		int encryptionFlags = sensorHeader.getEncryptionFlags();
		ByteBuffer sensorPacket =  decrypt(encryptionFlags, rawPacket);
		
		int readBytes = sensorHeader.getPackedSize(); // number of bytes read so far
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			// Set sensorPacket to correct position
			sensorPacket.position(readBytes);
			
			UniChannel uniChannel = new UniChannel(sensorPacket);
			Channel channel = new Channel(uniChannel);
			snapshot.addChannel(uniChannel.getHeader().getName(), channel);
			
			// Add to readBytes
			readBytes += uniChannel.getPackedSize();
		}
		
		return snapshot;
	}
}
