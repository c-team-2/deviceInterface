import java.util.Date;
import java.util.HashMap;

/**
 * The container for parsed sensor packet data.
 * @author Greg Clark
 *
 */
public class SensorSnapshot {
	private Sensor sensor;
	private Date timestamp;
	private HashMap<String, Channel> channels;
	
	SensorSnapshot(Sensor sensor, UniSensorHeader sensorHeader)
	{
		this.sensor = sensor;
		this.timestamp = new Date(sensorHeader.timestamp);
		this.channels = new HashMap<String, Channel>();
	}
	
	/**
	 * 
	 * @return the Date of when the device's sensor packet was constructed.
	 */
	Date getTimestamp() { return timestamp; }
	
	/**
	 * Add a Channel to this SensorSnapshot.
	 * @param name - name of the Channel to be added.
	 * @param channel - the Channel to be added.
	 */
	void addChannel(String name, Channel channel) {
		channels.put(name, channel);
	}
	
	/**
	 * 
	 * @return the Sensor that created this SensorSnapshot.
	 */
	Sensor getSensor() { return sensor; }
	
	/**
	 * 
	 * @param name - name of the Channel to get.
	 * @return the Channel, null if there is no Channel with the given name.
	 */
	Channel getChannel(String name) { return channels.get(name); }
}
