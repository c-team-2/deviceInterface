
import java.nio.ByteBuffer;

public class Tuple {
	private ByteBuffer data;
	private ElementMetaData[] elementMetaData;
	
	Tuple(ByteBuffer buffer, ElementMetaData[] elementMD) {
		data = buffer;
		elementMetaData = elementMD;
	}
	
	// TODO: Decide whether to throw exception on type mismatch
	byte getElementByte(int index) {
		return data.get(elementMetaData[index].getBufferIndex());
	}
	
	short getElementShort(int index) {
		return data.getShort(elementMetaData[index].getBufferIndex());
	}
	
	int getElementInt(int index) {
		return data.getInt(elementMetaData[index].getBufferIndex());
	}
	
	long getElementLong(int index) {
		return data.getLong(elementMetaData[index].getBufferIndex());
	}
	
	float getElementFloat(int index) {
		return data.getFloat(elementMetaData[index].getBufferIndex());
	}
	
	double getElementDouble(int index) {
		return data.getDouble(elementMetaData[index].getBufferIndex());
	}
	
	byte[] getElementByteArray(int index) {
		byte[] element = new byte[elementMetaData[index].getSize()];
		data.get(element, elementMetaData[index].getBufferIndex(), elementMetaData[index].getSize());
		return element;
	}
	
	ByteBuffer getElementByteBuffer(int index) {
		// Create new ByteBuffer that is a slice of this one
		data.position(elementMetaData[index].getBufferIndex());
		ByteBuffer element = data.slice();
		data.rewind();
		element.limit(elementMetaData[index].getSize());
		return element;
	}
}
