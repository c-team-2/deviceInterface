/**************************************************************
 This file is part of Kinect Sensor Architecture Development Project.

    Kinect Sensor Architecture Development Project is free software:
	you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Kinect Sensor Architecture Development Project is distributed in
	the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Kinect Sensor Architecture Development Project.  If
	not, see <http://www.gnu.org/licenses/>.
**************************************************************/
/**************************************************************
The work was done in joint collaboration with Cisco Systems Inc.
Copyright © 2012, Cisco Systems, Inc. and UCLA
*************************************************************/

package UnifyingAPI;

import java.nio.ByteBuffer;

/**
 * A data channel. Created by parsing a channel packet.
 *
 * @author Greg Clark
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
	
	/**
	 * Retrieves the channel's dimensions.
	 * @return the channel's dimensions
	 */
	public int[] getDimensions() { return dimensions; }
	
	/**
	 * Retrieves the size of a tuple in this channel in bytes.
	 * @return the size (in bytes) of a tuple
	 */
	public int getTupleSize() { return tupleSize; }
	
	/**
	 * Retrieves the number of tuples in this channel.
	 * @return the number of tuples in this channel.
	 */
	public long getNumberOfTuples() { return numTuples; }
	
	/**
	 * Retrieves an array of the metadata of the elements in a tuple.
	 * @return the array of metadata objects per element in a tuple
	 */
	public ElementMetaData[] getElementMetaData() { return elementMetaData; }
	
	/**
	 * Retrieves the frequency in Hz at which this channel can be updated.
	 * @return the frequency in Hz of the channel.
	 */
	public double getFrequency() { return frequency; }
	
	/**
	 * Retrieves the name of the channel.
	 * @return the name of the channel.
	 */
	public String getName() { return name; }
	
	/**
	 * Constructs a Channel from a {@link UnifyingAPI.UniChannel <code>UniChannel</code>}
	 * @param uniChannel the <code>UniChannel</code> from which to construct a <code>Channel</code>
	 */
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
		// assuming given sensor packet is byte aligned per element
		// need to convert 3, 5, 6, and 7 byte integers into primitives
		
		// Create null byte arrays
		byte[][] nullBytes = new byte[elementMetaData.length][];
		for (int i = 0; i < elementMetaData.length; ++i)
		{
			int packedSize = descriptors[i].getSize();
			int numNullBytes = elementMetaData[i].getSize() - packedSize;
			nullBytes[i] = new byte[numNullBytes];
		}
		
		this.data = ByteBuffer.allocate((int) (tupleSize * numTuples));
		int bytesRead = 0;
		int bytesWritten = 0;
		ByteBuffer originalData = uniChannel.getData();
		for (int tupleCount = 0; tupleCount < numTuples; ++tupleCount)
		{
			for (int elementCount = 0; elementCount < elementMetaData.length; ++elementCount)
			{
				originalData.position(bytesRead);
				this.data.position(bytesWritten);
				
				// Create element ByteBuffer
				ByteBuffer element = originalData.slice();
				int packedSize = descriptors[elementCount].getSize();
				element.limit(packedSize);
				
				// Write null bytes to highest order bytes
				this.data.put(nullBytes[elementCount]);
				
				// Write element to parsed data buffer
				this.data.put(element);
				
				// Increment byte counts
				bytesRead += packedSize;
				bytesWritten += elementMetaData[elementCount].getSize();
			}
		}
	}

	/**
	 * Sets the dimensions of the channel. Returns true if properly set
	 * @param args the new dimensions to set. The final dimension should be the one closest together in memory
	 * @return true if and only if product of args is equal to this channel's number of Tuples
	 * @throws Exception if the product of the given dimensions does not equal the number of tuples in the channel
	 */
	public void setDimensions(int... args) throws Exception
	{
		int dimensionProducts[] = new int[args.length];
		dimensionProducts[args.length-1] = this.tupleSize;
		for (int i = args.length-2; i >= 0 ; ++i) {
			dimensionProducts[i] = dimensionProducts[i+1] * args[i+1];
		}
		
		// Check if final product of dimensions is equal to the number of Tuples in this channel
		if (dimensionProducts[0] / tupleSize != this.numTuples)
		{
			throw new Exception("The given dimensions do not match the number of tuples");
		}
		else
		{
			for (int i = 0; i < args.length; ++i)
			{
				this.dimensions[i] = args[i];
				this.dimProducts[i] = dimensionProducts[i];
			}
		}
	}
	
	/** 
	 * Get tuple using integer dimensions.
	 * @param indices of tuple, 0-indexed. e.g. 639,479 for last pixel in a VGA image
	 * @return the <code>Tuple</code>
	 * @throws IllegalArgumentException if the dimensionality is higher than this
	 *  <code>Channel</code>'s dimensionality or if the calculated index is too high
	 */
	public Tuple getTuple(int... indices) throws IllegalArgumentException {
		// Throw an exception if too many arguments
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
		
		// Throw an exception if the calculated index is too high for the number of tuples
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