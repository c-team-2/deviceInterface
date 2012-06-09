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
		
		int packedSize = sizeInBytes;
		
		// Set the UniType and size of the element
		if (descriptor.isInteger()) 
		{
			switch(packedSize) 
			{
			case 1:
				this.type = UniType.int8;
				this.size = 1;
				break;
			case 2:
				this.type = UniType.int16;
				this.size = 2;
				break;
			case 3:
			case 4:
				this.type = UniType.int32;
				this.size = 4;
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				this.type = UniType.int64;
				this.size = 8;
				break;
			default:
				this.type = UniType.int_nonprimitive;
				this.size = packedSize;
			}
		}
		else
		{
			switch(sizeInBytes)
			{
			case 4:
				this.type = UniType.float32;
				this.size = 4;
				break;

			case 8:
				this.type = UniType.float64;
				this.size = 8;
				break;
			default:
				// Return nonprimitive if not a float or double
				this.type = UniType.float_nonprimitive;
				this.size = packedSize;
			}
		}
		
		this.bufferIndex = bufferIndex;
	}
	
	private UniType type;		// Type of element
	private int size;			// Size of element in bytes after parsing
	private int bufferIndex;	// index in ByteBuffer in bytes of this element relative to beginning of tuple
	
	/**
	 * Retrieves the <code>UniType</code> of the element.
	 * @return the <code>UniType</code> of the element
	 */
	public UniType getType() { return type; }
	
	/**
	 * Retrieves the size of the element in bytes
	 * @return the size of the element in bytes.
	 */
	public int getSize() { return size; }
	
	/**
	 * Retrieves the 0-based byte index of the element in a <code>Tuple</code>'s 
	 * data buffer.
	 * @return the index of the element in a <code>Tuple</code>.
	 */
	public int getBufferIndex() { return bufferIndex; }
}