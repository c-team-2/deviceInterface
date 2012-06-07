package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * A tuple from a <code>Channel</code>.
 * @author Greg Clark
 *
 */
public class Tuple {
	private ByteBuffer data;
	private ElementMetaData[] elementMetaData;
	
	/**
	 * Construct a Tuple from a data buffer and an array of element metadata objects
	 * @param data a ByteBuffer containing the <code>Tuple</code>'s data
	 * @param elementMetaData an array of element metadata objects, in the order 
	 * which the elements appear in the data buffer
	 */
	public Tuple(ByteBuffer data, ElementMetaData[] elementMetaData) {
		this.data = data;
		this.elementMetaData = elementMetaData;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>byte</code>.
	 */
	public byte getElementByte(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int8)
		{
			throw new Exception("Element is not of type byte");
		}
		return data.get(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to a <code>byte</code>. Returns 0 if the element is a nonprimitive type.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to a <code>byte</code>
	 */
	public byte getElementCastToByte(int index) {
		byte element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = (byte) data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = (byte) data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = (byte) data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (byte) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = (byte) data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}

	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>short</code>.
	 */
	public short getElementShort(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int16)
		{
			throw new Exception("Element is not of type short");
		}
		return data.getShort(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to a <code>short</code>. Returns 0 if the element is a nonprimitive type
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to a <code>short</code>
	 */
	public short getElementCastToShort(int index) {
		short element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = (short) data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = (short) data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = (short) data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (short) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = (short) data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>int</code>.
	 */
	public int getElementInt(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int32)
		{
			throw new Exception("Element is not of type int");
		}
		return data.getInt(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to an <code>int</code>. Returns 0 if the element is a nonprimitive type
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to an <code>int</code>
	 */
	public int getElementCastToInt(int index) {
		int element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = (int) data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = (int) data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = (int) data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (int) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = (int) data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>long</code>.
	 */
	public long getElementLong(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.int64)
		{
			throw new Exception("Element is not of type long");
		}
		return data.getLong(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to a <code>long</code>. Returns 0 if the element is a nonprimitive type
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to a <code>long</code>
	 */
	public long getElementCastToLong(int index) {
		long element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = (long) data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = (long) data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = (long) data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (long) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = (long) data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>float</code>.
	 */
	public float getElementFloat(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.float32)
		{
			throw new Exception("Element is not of type float");
		}
		return data.getFloat(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to a <code>float</code>. Returns 0 if the element is a nonprimitive type
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to a <code>float</code>
	 */
	public float getElementCastToFloat(int index) {
		float element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = (float) data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = (float) data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = (float) data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = (float) data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (float) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = (float) data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element
	 * @throws Exception if element is not of type <code>double</code>.
	 */
	public double getElementDouble(int index) throws Exception {
		if (elementMetaData[index].getType() != UniType.float64)
		{
			throw new Exception("Element is not of type double");
		}
		return data.getDouble(elementMetaData[index].getBufferIndex());
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code> cast 
	 * to a <code>double</code>. Returns 0 if the element is a nonprimitive type
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element cast to a <code>double</code>
	 */
	public double getElementCastToDouble(int index) {
		double element = 0;
		
		switch (elementMetaData[index].getType())
		{
		case int8:
			element = (double) data.get(elementMetaData[index].getBufferIndex());
			break;
		case int16:
			element = (double) data.getShort(elementMetaData[index].getBufferIndex());
			break;
		case int32:
			element = (double) data.getInt(elementMetaData[index].getBufferIndex());
			break;
		case int64:
			element = (double) data.getLong(elementMetaData[index].getBufferIndex());
			break;
		case float32:
			element = (double) data.getFloat(elementMetaData[index].getBufferIndex());
			break;
		case float64:
			element = data.getDouble(elementMetaData[index].getBufferIndex());
			break;
		}
		
		return element;
	}
	
	/**
	 * Returns the element at the given index in this <code>Tuple</code>.
	 * @param index the 0-based index of the element in this <code>Tuple</code>.
	 * @return the element as a <code>ByteBuffer</code>
	 */
	public ByteBuffer getElementByteBuffer(int index) {
		// Create new ByteBuffer that is a slice of this one
		data.position(elementMetaData[index].getBufferIndex());
		ByteBuffer element = data.slice();
		data.rewind();
		element.limit(elementMetaData[index].getSize());
		return element;
	}
}
