package UnifyingAPI;

/**
 * Metadata for an element.
 * @author Greg Clark
 *
 */
public class ElementMetaData {
	
	/**
	 * Construct a metadata object from a tuple element descriptor. The caller 
	 * must provide the index of the element in a tuple's data buffer.
	 * @param descriptor the tuple element descriptor
	 * @param bufferIndex the byte index in a <code>Tuple</code>'s data buffer
	 * where the element corresponding to this metadata object begins.
	 */
	public ElementMetaData(UniElementDescriptor descriptor, int bufferIndex) {
		
		int sizeInBits = descriptor.getSize() << (descriptor.isSizedInBytes()?3:0);
		int sizeInBytes = (sizeInBits >> 3) + (((sizeInBits & 7) == 0)?0:1);
		
		this.size = sizeInBytes;
		
		// TODO: decide whether to handle unsigned
		if (descriptor.isInteger()) 
		{
			switch(sizeInBytes) 
			{
			case 1:
				this.type = UniType.int8;
				break;
			case 2:
				this.type = UniType.int16;
				break;
			case 3:
			case 4:
				this.type = UniType.int32;
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				this.type = UniType.int64;
				break;
			default:
				this.type = UniType.int_nonprimitive;
			}
		}
		else
		{
			switch(sizeInBytes)
			{
			case 1:
			case 2:
			case 3:
			case 4:
				this.type = UniType.float32;
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				this.type = UniType.float64;
				break;
			default:
				this.type = UniType.float_nonprimitive;
			}
		}
		
		this.bufferIndex = bufferIndex;
	}
	
	private UniType type;		// Type of element
	private int size;			// Size of element in bytes
	private int bufferIndex;	// index in ByteBuffer in bytes of this element relative to beginning of tuple
	
	public UniType getType() { return type; }
	public int getSize() { return size; }
	public int getBufferIndex() { return bufferIndex; }
}