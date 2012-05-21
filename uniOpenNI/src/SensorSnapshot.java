import java.util.Date;
import java.util.HashMap;


public class SensorSnapshot {
	private Sensor sensor;
	private Date timestamp;
	private HashMap<String, Channel> channels;
	
	SensorSnapshot(Sensor sensor, UniSensorHeader sensorHeader)
	{
		this.sensor = sensor;
		this.timestamp = new Date(sensorHeader.timestamp);
	}
	
	Date getTimestamp() { return timestamp; }
	
	void addChannel(String name, Channel channel) {
		channels.put(name, channel);
	}
	
	Sensor getSensor() { return sensor; }
}
