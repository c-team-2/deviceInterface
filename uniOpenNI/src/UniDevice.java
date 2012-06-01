/**
 * Represents a device that implements the Unified API.
 * @author Greg Clark
 *
 */
public abstract class UniDevice {
	/**
	 * Constructs and returns a new sensor packet as a byte array.
	 * @return the sensor packet
	 */
	abstract byte[] getSensorPacket();

}
