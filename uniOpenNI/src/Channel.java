import java.nio.ByteBuffer;
/**
 * Representation of a Channel data after parsing a sensor packet.
 *
 */
public class Channel {
	private ByteBuffer data;	// The parsed data, tuple elements are byte-aligned
	private int[] dimensions;	// The dimensions of how to interpret data. ex: [640; 480] for VGA
	private int[] dimProducts;	/* Used for quickly calculating index. 
									dimProducts[dimProducts.length-1] = tupleSize
									dimProducts[i] = dimProducts[i+1] * dimensions[i+1] */
	private int tupleSize;		// size of tuple in bytes
	private long numTuples;		// number of tuples in Channel
	private ElementMetaData[] elementMetaData;	// metadata used when accessing elements in a tuple
	private double frequency;	// Frequency in Hz at which this channel can update
	private String name;
	
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
	
	public Channel(UniChannel uniChannel) {
		// TODO Auto-generated constructor stub
		UniChannelHeader header = uniChannel.getHeader();
		UniElementDescriptor descriptors[] = header.getElementDescriptors();
		this.frequency = header.getFrequency();
		this.numTuples = header.getNumberTuples();
		this.name = header.getName();
		
	}

	/** 
	 * Get tuple using integer dimensions.
	 * @param indices of tuple, 0-indexed. e.g. 639,479 for last pixel in a VGA image
	 * @return Tuple
	 * @throws IllegalArgumentException if the dimensionality is higher than this Channel's dimensionality
	 * 	or if the calculated index is too high
	 */
	Tuple getTuple(int... indices) throws IllegalArgumentException {
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