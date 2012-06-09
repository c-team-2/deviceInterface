/**************************************************************
 This file is part of Kinect Sensor Architecture Development Project.

    Kinect Sensor Architecture Development Project is free software:
	you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Kinect Sensor Architecture Development Project is distributed in
	the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Kinect Sensor Architecture Development Project.  If
	not, see <http://www.gnu.org/licenses/>.
**************************************************************/
/**************************************************************
The work was done in joint collaboration with Cisco Systems Inc.
Copyright © 2012, Cisco Systems, Inc. and UCLA
*************************************************************/

package UnifyingAPI;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * A container for parsed sensor packet data.
 * @author Greg Clark, Richard Yu
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
	 * @return the <code>Date</code> when the device's sensor packet was constructed.
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
	 * Retrieves the <code>Sensor</code> that created this <code>SensorSnapshot</code>.
	 * @return the <code>Sensor</code> that created this <code>SensorSnapshot</code>
	 */
	public Sensor getSensor() { return sensor; }
	
	/**
	 * Retrieves the <code>Channel</code> with the given name.
	 * @param name name of the <code>Channel</code> to get.
	 * @return the <code>Channel</code>, <code>null</code> if there is no 
	 * <code>Channel</code> with the given name.
	 */
	public Channel getChannel(String name) { return channels.get(name); }
	
	/**
	 * Retrieves a <code>Set</code> containing the names of each channel
	 * present in the SensorSnapshot
	 * @return the <code>Set</code>, of channel names.
	 */
	public Set<String> getChannelNames()
	{
		//Create a copy to ensure no damage is done to the actual key Set
		Set<String> s = new HashSet<String>(channels.keySet());
		return s;
	}
}
