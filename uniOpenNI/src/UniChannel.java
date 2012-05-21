import java.nio.ByteBuffer;


public class UniChannel {
	private ByteBuffer data;
	private UniChannelHeader header;
	
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
	
	ByteBuffer getData() { return data; }
	UniChannelHeader getHeader() { return header; }
}
