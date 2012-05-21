
import java.nio.ByteBuffer;

public class Tuple {
	private ByteBuffer data;
	private ElementMetaData[] elementMetaData;
	
	Tuple(ByteBuffer buffer, ElementMetaData[] elementMD) {
		data = buffer;
		elementMetaData = elementMD;
	}
	
	// TODO: Decide whether to throw exception on type mismatch
	int getElementInt(int index) {
		return data.getInt(elementMetaData[index].bufferIndex);
	}
	
	long getElementLong(int index) {
		return data.getLong(elementMetaData[index].bufferIndex);
	}
	
	byte[] getElementByteArray(int index) {
		byte[] element = new byte[elementMetaData[index].size];
		data.get(element, elementMetaData[index].bufferIndex, elementMetaData[index].size);
		return element;
	}
	
	ByteBuffer getElementByteBuffer(int index) {
		// Create new ByteBuffer that is a slice of this one
		data.position(elementMetaData[index].bufferIndex);
		ByteBuffer element = data.slice();
		data.rewind();
		element.limit(elementMetaData[index].size);
		return element;
	}
}
