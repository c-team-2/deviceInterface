
import java.nio.ByteBuffer;

/**
 * A tuple from a Channel.
 * @author Greg Clark
 *
 */
public class Tuple {
	private ByteBuffer data;
	private ElementMetaData[] elementMetaData;
	
	Tuple(ByteBuffer buffer, ElementMetaData[] elementMD) {
		data = buffer;
		elementMetaData = elementMD;
	}
	
	byte getElementByte(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int8)
		{
			throw new Exception("Element is not of type byte");
		}
		return data.get(elementMetaData[index].getBufferIndex());
	}
	
	short getElementShort(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int16)
		{
			throw new Exception("Element is not of type short");
		}
		return data.getShort(elementMetaData[index].getBufferIndex());
	}
	
	int getElementInt(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int32)
		{
			throw new Exception("Element is not of type int");
		}
		return data.getInt(elementMetaData[index].getBufferIndex());
	}
	
	long getElementLong(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int64)
		{
			throw new Exception("Element is not of type long");
		}
		return data.getLong(elementMetaData[index].getBufferIndex());
	}
	
	float getElementFloat(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.float32)
		{
			throw new Exception("Element is not of type float");
		}
		return data.getFloat(elementMetaData[index].getBufferIndex());
	}
	
	double getElementDouble(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.float64)
		{
			throw new Exception("Element is not of type double");
		}
		return data.getDouble(elementMetaData[index].getBufferIndex());
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
