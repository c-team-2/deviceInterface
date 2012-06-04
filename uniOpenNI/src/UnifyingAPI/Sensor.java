package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * The interface used to retrieve and parse sensor packets from a <code>UniDevice</code>. 
 * @author Greg Clark, Richard Yu
 *
 */
public class Sensor {
	
	private UniDevice device;
	private byte APIversion;
	private short vendorID;
	private short productID;
	private double frequency;
	private UniCrypt decryptor;
	
	/**
	 * Constructs a Sensor from a UniDevice. Will block while waiting to get a 
	 * sensor packet header from the device.
	 * @param device
	 */
	public Sensor(UniDevice device)
	{
		this.device = device;
		byte[] sensorPacketHeader = this.device.getSensorHeader();
		UniSensorHeader header = new UniSensorHeader(ByteBuffer.wrap(sensorPacketHeader));
		this.APIversion = header.getVersion();
		this.vendorID = header.getVendorID();
		this.productID = header.getProductID();
		this.frequency = header.getFrequency();
		this.decryptor = null;
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
		ByteBuffer sensorPacket;
		if (decryptor != null)
		{
			int encryptionFlags = sensorHeader.getEncryptionFlags();
			sensorPacket =  decryptor.decrypt(encryptionFlags, rawPacket);
		}
		else
			sensorPacket = rawPacket;
		
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
	
	/**
	 * Retrieves the version number of the Unifying API being used.
	 * @return the version number of the API
	 */
	public byte getAPIVersion() { return APIversion; }
	
	/**
	 * Retrieves the vendor ID of the device.
	 * @return the vendor ID of the device
	 */
	public short getVendorID() { return vendorID; }
	
	/**
	 * Retrieves the product ID of the device.
	 * @return the product ID of the device
	 */
	public short getProductID() { return productID; }
	
	/**
	 * Retrieves the highest update frequency in Hz among all the channels .
	 * @return the highest update frequency in Hz
	 */
	public double getFrequency() { return frequency; }
	
	/**
	 * Sets the decryptor to use on encrypted sensor packets.
	 * @param decryptor the decryptor to use
	 */
	public void setDecryptor(UniCrypt decryptor)
	{
		this.decryptor = decryptor;
	}
}
