import java.nio.ByteBuffer;

class UniSensorHeader {
		byte version;
		short vendorID;
		short productID;
		short numChannels;
		long timestamp;
		double frequency;
		
		UniSensorHeader(ByteBuffer sensorPacket) {
			version = sensorPacket.get();
			sensorPacket.get();
			vendorID = sensorPacket.getShort();
			productID = sensorPacket.getShort();
			numChannels = sensorPacket.getShort();
			timestamp = sensorPacket.getLong();
			frequency = sensorPacket.getDouble();
		}
		
		UniSensorHeader(byte version, short vendorID, short productID, 
				short numChannels, long timestamp, double frequency) {
			this.version = version;
			this.vendorID = vendorID;
			this.productID = productID;
			this.numChannels = numChannels;
			this.timestamp = timestamp;
			this.frequency = frequency;
		}
		
		boolean PackIntoByteBuffer(ByteBuffer sensorPacket) {
			sensorPacket.put(version);
			sensorPacket.put((byte)0x00);
			sensorPacket.putShort(vendorID);
			sensorPacket.putShort(productID);
			sensorPacket.putShort(numChannels);
			sensorPacket.putLong(timestamp);
			sensorPacket.putDouble(frequency);
			return true;
		}
	}