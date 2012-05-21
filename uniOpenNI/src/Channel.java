import java.nio.ByteBuffer;

public class Channel {
	private ByteBuffer data;
	private int[] dimensions;
	private int[] dimProducts;
	private int tupleSize;
	private long numTuples;
	private ElementMetaData[] elementMetaData;
	
	Channel(ByteBuffer data, int[] dimensions, ElementMetaData[] elementMD, long numTuples) {
		this.elementMetaData = elementMD;
		this.data = data;
		this.dimensions = dimensions;
		this.numTuples = numTuples;
		
		// Determine tuple size
		int tupleSize = 0;
		for (int i = 0; i < elementMD.length; ++i) {
			tupleSize += elementMD[0].size;
		}
		
		dimProducts[dimensions.length-1] = tupleSize;
		for (int i = dimensions.length-2; i > 0 ; ++i) {
			dimProducts[i] = dimProducts[i+1] * dimensions[i+1];
		}
	}
	
	/** Get tuple using integer dimensions
	 * 
	 * @param indices of tuple, 0-indexed. e.g. 639,479 for last pixel in a VGA image
	 * @return Tuple
	 */
	Tuple getTuple(int... indices) {
		// TODO: Decide whether to check bounds of each dimension or just final index
		if (indices.length > dimensions.length)
		{
			String message = String.format("Too many arguments. Asked for %d dimensions, Channel only has %d dimensions\n",
					indices.length, dimensions.length);
			throw new IllegalArgumentException(message);
		}
		
		// Find index of tuple in ByteBuffer
		int index = 0;
		for (int i = 0; i < indices.length; ++i)
		{
			index += (indices[i] * dimProducts[i]);
		}
		
		if (index >= numTuples * tupleSize) 
		{
			String message = String.format("Data index too high. Asked for index %d, max index %d\n",
					index, (numTuples - 1) * tupleSize);
			throw new IllegalArgumentException(message);
		}
		
		// Create new ByteBuffer
		data.position(index);
		ByteBuffer buffer = data.slice();
		data.rewind();
		buffer.limit(tupleSize);
		
		Tuple tuple = new Tuple(buffer, elementMetaData);
		return tuple;
	}
}