import java.nio.ByteBuffer;


public class UniChannel {
	ByteBuffer data;
	UniChannelHeader header;
	
	public UniChannel(ByteBuffer sensorPacket) {		
		header = new UniChannelHeader(sensorPacket);
		data = sensorPacket.slice().asReadOnlyBuffer();
	}
	
	public UniChannel(UniChannelHeader header, ByteBuffer data) {		
		this.header = header;
		this.data = data;
	}

	boolean packIntoByteBuffer(ByteBuffer sensorPacket) 
	{
		header.packIntoByteBuffer(sensorPacket);
		sensorPacket.put(data);
		return true;
	}
}
