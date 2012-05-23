import java.nio.ByteBuffer;


public class Sensor {
	
	private UniDevice device;
	
	Sensor(UniDevice device)
	{
		this.device = device;
	}
	
	SensorSnapshot getSensorSnapshot()
	{
		// Get sensor packet from device driver
		ByteBuffer sensorPacket = ByteBuffer.wrap(device.getSensorPacket()).asReadOnlyBuffer();
		sensorPacket.rewind();
		
		// Create new SensorSnapshot
		UniSensorHeader sensorHeader = new UniSensorHeader(sensorPacket); 
		SensorSnapshot snapshot = new SensorSnapshot(this, sensorHeader);
		
		// Parse sensor packet into Channels and add to SensorSnapshot
		int numChannels = sensorHeader.numChannels;
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
