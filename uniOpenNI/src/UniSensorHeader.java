import java.nio.ByteBuffer;

/**
 * Represents the header of a sensor packet.
 * @author Greg Clark
 *
 */
class UniSensorHeader {
		private byte version;
		private short vendorID;
		private short productID;
		private short numChannels;
		private long timestamp;
		private double frequency;
		private int encryptionFlags;
		
		public byte getVersion() { return version; }
		public short getVendorID() { return vendorID; }
		public short getProductID() { return productID; }
		public short getNumChannels() { return numChannels; }
		public long getTimestamp() { return timestamp; }
		public double getFrequency() { return frequency; }
		public int getEncryptionFlags() { return encryptionFlags; }
		
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
			encryptionFlags = sensorPacket.getInt();
		}
		
		public UniSensorHeader(byte version, short vendorID, short productID, 
				short numChannels, long timestamp, double frequency, int encryptionFlags) {
			this.version = version;
			this.vendorID = vendorID;
			this.productID = productID;
			this.numChannels = numChannels;
			this.timestamp = timestamp;
			this.frequency = frequency;
			this.encryptionFlags = encryptionFlags;
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
			sensorPacket.putInt(encryptionFlags);
			return true;
		}
		
		/**
		 * Returns the size in bytes that this sensor header would require if written in 
		 * the sensor packet format.
		 * @return number of bytes needed to store this sensor header in a sensor packet
		 */
		public int getPackedSize()
		{
			return 32;
		}
	}