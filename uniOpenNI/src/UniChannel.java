import java.nio.ByteBuffer;


public class UniChannel {

	
	ByteBuffer data;
	
	public UniChannel(ByteBuffer sensorPacket) {
		// TODO Auto-generated constructor stub
		
		UniChannelHeader header = new UniChannelHeader(sensorPacket);
	}

}
