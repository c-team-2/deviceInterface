import java.nio.ByteBuffer;
import java.util.Date;


public class Sensor {
	
	private UniDevice device;
	
	Sensor(UniDevice device)
	{
		this.device = device;
	}
	
	SensorSnapshot getSensorSnapshot()
	{
		ByteBuffer sensorPacket = device.getSensorPacket();
		UniSensorHeader sensorHeader = new UniSensorHeader(sensorPacket); 
		SensorSnapshot snapshot = new SensorSnapshot();
		
		return snapshot;
	}
}
