package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * The header of a sensor packet.
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
		
		/**
		 * Retrieves the version number of the Unifying API being used
		 * @return the version number of the API
		 */
		public byte getVersion() { return version; }
		
		/**
		 * Retrieves the vendor ID of the device.
		 * @return the vendor ID of the device
		 */
		public short getVendorID() { return vendorID; }
		
		/**
		 * Retrieves the product ID of the device.
		 * @return the product ID of the device
		 */
		public short getProductID() { return productID; }
		
		/**
		 * Retrieves the number of channels in the associated sensor packet
		 * @return the number of channels in the sensor packet
		 */
		public short getNumChannels() { return numChannels; }
		
		/**
		 * Retrieves the timestamp for when the sensor packet was created.
		 * @return the timestamp for when the senor packet was created
		 */
		public long getTimestamp() { return timestamp; }
		
		/**
		 * Retreives the highest update frequency in Hz among all the channels 
		 * @return the highest update frequency in Hz
		 */
		public double getFrequency() { return frequency; }
		
		/**
		 * Retrieves the integer containing the flags for determining which 
		 * encryption method is used on the data channels in the sensor packet
		 * @return the encryption flags as an integer
		 */
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
		
		/**
		 * Construct a sensor packet header with the given characteristics
		 * @param version the version of the Unifying API
		 * @param vendorID the 16-bit vendor ID of the device
		 * @param productID the 16-bit product ID of the device
		 * @param numChannels the number of channels in the sensor packet
		 * @param timestamp the timestamp at which the sensor packet was created
		 * @param frequency the highest update frequency in Hz among the channels
		 * in the sensor packet
		 * @param encryptionFlags flags for determining the encryption method
		 */
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