package UnifyingAPI;

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
	
	public SensorSnapshot(Sensor sensor, UniSensorHeader sensorHeader)
	{
		this.sensor = sensor;
		this.timestamp = new Date(sensorHeader.getTimestamp());
		this.channels = new HashMap<String, Channel>();
	}
	
	/**
	 * Returns the timestamp in the sensor packet from which this 
	 * <code>SensorSnapshot</code> was constructed.
	 * @return the Date of when the device's sensor packet was constructed.
	 */
	public Date getTimestamp() { return timestamp; }
	
	/**
	 * Add a <code>Channel</code> to this <code>SensorSnapshot</code>.
	 * @param name name of the <code>Channel</code> to be added.
	 * @param channel the <code>Channel</code> to be added.
	 */
	public void addChannel(String name, Channel channel) {
		channels.put(name, channel);
	}
	
	/**
	 * 
	 * @return the <code>Sensor</code> that created this <code>SensorSnapshot</code>.
	 */
	public Sensor getSensor() { return sensor; }
	
	/**
	 * 
	 * @param name name of the <code>Channel</code> to get.
	 * @return the <code>Channel</code>, null if there is no <code>Channel</code> with the given name.
	 */
	public Channel getChannel(String name) { return channels.get(name); }
}
