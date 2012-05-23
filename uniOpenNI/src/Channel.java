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
	
	int[] getDimensions() { return dimensions; }
	int getTupleSize() { return tupleSize; }
	long getNumberOfTuples() { return numTuples; }
	ElementMetaData[] getElementMetaData() { return elementMetaData; }
	double getFrequency() { return frequency; }
	String getName() { return name; }
	
	public Channel(UniChannel uniChannel) {
		UniChannelHeader header = uniChannel.getHeader();
		UniElementDescriptor descriptors[] = header.getElementDescriptors();
		
		
		this.frequency = header.getFrequency();
		this.numTuples = header.getNumberTuples();
		this.name = header.getName();
		
		// Set element metadata
		elementMetaData = new ElementMetaData[descriptors.length];
		int bufferIndex = 0;
		for (int i = 0; i < descriptors.length; ++i)
		{
			elementMetaData[i] = new ElementMetaData(descriptors[i], bufferIndex);
			bufferIndex += elementMetaData[i].getSize();
		}
		
		// Set tupleSize
		this.tupleSize = bufferIndex;
		
		// Set 1D dimension
		// TODO: only takes int for dimension. ByteBuffer can only allocate 2GB anyway
		this.dimensions = new int[1];
		this.dimensions[0] = (int) this.numTuples;
		this.dimProducts = new int[1];
		this.dimProducts[0] = this.tupleSize;
		
		// Parse data
		// TODO: assuming given sensor packet is byte aligned per element, just using given bytebuffer
		this.data = uniChannel.getData();
	}

	/**
	 * Sets the dimensions of the channel. Returns true if properly set
	 * @param args - dimensions, final dimension should be the one closest together in memory
	 * @return true if and only if product of args is equal to this channel's number of Tuples
	 */
	boolean setDimensions(int... args)
	{
		int dimensionProducts[] = new int[args.length];
		dimensionProducts[args.length-1] = this.tupleSize;
		for (int i = args.length-2; i >= 0 ; ++i) {
			dimensionProducts[i] = dimensionProducts[i+1] * args[i+1];
		}
		
		// Check if final product of dimensions is equal to the number of Tuples in this channel
		if (dimensionProducts[0] / tupleSize != this.numTuples)
		{
			return false;
		}
		else
		{
			for (int i = 0; i < args.length; ++i)
			{
				this.dimensions[i] = args[i];
				this.dimProducts[i] = dimensionProducts[i];
			}
		}
		
		return true;
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