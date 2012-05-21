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
		ByteBuffer sensorPacket = device.getSensorPacket();
		
		// Create new SensorSnapshot
		UniSensorHeader sensorHeader = new UniSensorHeader(sensorPacket); 
		SensorSnapshot snapshot = new SensorSnapshot(this, sensorHeader);
		
		// Parse sensor packet into Channels and add to SensorSnapshot
		int numChannels = sensorHeader.numChannels;
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			// Parse packed data into byte-aligned elements
			UniChannel uniChannel = new UniChannel(sensorPacket);
			
			// 
			Channel channel = new Channel(uniChannel);
		}
		
		return snapshot;
	}
}
