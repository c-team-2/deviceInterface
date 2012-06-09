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
 * A tuple element descriptor. Describes the type of a single element ina tuple.
 * @author Greg Clark
 *
 */
public class UniElementDescriptor {
	/**
	 * Construct a tuple element descriptor with the given characteristics
	 * @param integer true if the element is an integral type
	 * @param sizeUnitBytes true if the given size is in bytes, false if the size is in bits
	 * @param size the size of the element, in either byte or bits as specified 
	 * by the <code>sizeUnitBytes</code> parameter 
	 */
	public UniElementDescriptor(boolean integer, boolean sizeUnitBytes, byte size) {
		this.integer = integer;
		this.sizeUnitBytes = sizeUnitBytes;
		this.size = size;
		byte descriptor = (byte) ((this.integer?1:0) << 7);
		descriptor |= (byte) ((this.sizeUnitBytes?1:0) << 6);
		descriptor |= (byte) (this.size);
		this.descriptor = descriptor;
	}
	
	/**
	 * Construct a tuple element descriptor from a byte.
	 * @param descriptor the byte representing a tuple element descriptor from a 
	 * channel packet header.
	 */
	public UniElementDescriptor(byte descriptor) {
		this.integer = ((descriptor & 128) == 128)?true:false;
		this.sizeUnitBytes = ((descriptor & 64) == 64)?true:false;
		this.size = (byte) (descriptor & 0x1F);
		this.descriptor = descriptor;
	}
	
	/**
	 * Tells whether the corresponding element is an integral type.
	 * @return true if and only if the element is an integral type
	 */
	public boolean isInteger() { return integer; }
	
	/**
	 * Tells whether the elements size is given in bytes or bits
	 * @return true if the elements given size is in bytes
	 */
	public boolean isSizedInBytes() { return sizeUnitBytes; }
	
	/**
	 * Retrieves the size of the corresponding element.
	 * @return the size of the corresponding element. The unit is determined from 
	 * isSizedInBytes()
	 */
	public byte getSize() { return size; }
	
	/**
	 * Retrieves the tuple element descriptor as written in a channel packet header.
	 * @return the tuple element descriptor as a byte
	 */
	public byte getDescriptor() { return descriptor; }
	
	private boolean integer;
	private boolean sizeUnitBytes;
	private byte size;
	private byte descriptor;
}
