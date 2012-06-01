import java.nio.ByteBuffer;

/**
 * Represents the header of a sensor packet.
 * @author Greg Clark
 *
 */
class UniSensorHeader {
		byte version;
		short vendorID;
		short productID;
		short numChannels;
		long timestamp;
		double frequency;
		
		/**
		 * Construct a <code>UniSensorHeader</code> from a sensor packet.
		 * @param sensorPacket - the <code>ByteBuffer</code> containing the sensor packet, 
		 * with its position at the beginning of the sensor packet
		 */
		public UniSensorHeader(ByteBuffer sensorPacket) {
			version = sensorPacket.get();
			sensorPacket.get();
			vendorID = sensorPacket.getShort();
			productID = sensorPacket.getShort();
			numChannels = sensorPacket.getShort();
			timestamp = sensorPacket.getLong();
			frequency = sensorPacket.getDouble();
		}
		
		public UniSensorHeader(byte version, short vendorID, short productID, 
				short numChannels, long timestamp, double frequency) {
			this.version = version;
			this.vendorID = vendorID;
			this.productID = productID;
			this.numChannels = numChannels;
			this.timestamp = timestamp;
			this.frequency = frequency;
		}
		
		/**
		 * Write the sensor packet header into a <code>ByteBuffer</code> at the <code>ByteBuffer</code>'s position.
		 * @param sensorPacket the <code>ByteBuffer</code> to write the sensor packet header into, 
		 * positioned where the sensor packet header should start.
		 */
		public boolean packIntoByteBuffer(ByteBuffer sensorPacket) {
			sensorPacket.put(version);
			sensorPacket.put((byte)0x00);
			sensorPacket.putShort(vendorID);
			sensorPacket.putShort(productID);
			sensorPacket.putShort(numChannels);
			sensorPacket.putLong(timestamp);
			sensorPacket.putDouble(frequency);
			return true;
		}
		
		/**
		 * Returns the size in bytes that this sensor header would require if written in 
		 * the sensor packet format.
		 * @return number of bytes needed to store this sensor header in a sensor packet
		 */
		public int getPackedSize()
		{
			return 24;
		}
	}