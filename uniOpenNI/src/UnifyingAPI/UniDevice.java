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
import java.util.LinkedList;

/**
 * A device that implements the Unifying API.
 * @author Greg Clark
 *
 */
public abstract class UniDevice {
	
	/**
	 * Device driver devlopers should call this constructor when they subclass <code>UniDevice</code>
	 * @param vendorID the USB vendor ID for the device
	 * @param productID the USB product ID for the device
	 * @param frequency the highest update frequency among all data channels
	 */
	protected UniDevice(short vendorID, short productID, double frequency)
	{
		this.encryptor = new UniCrypt();
		this.channels = new LinkedList<UniChannel>();
		this.vendorID = vendorID;
		this.productID = productID;
		this.frequency = frequency;
		this.encryptionFlags = 0;
	}
	
	/**
	 * Constructs and returns a new sensor packet as a ByteBuffer.
	 * @return the sensor packet
	 */
	public final ByteBuffer getSensorPacket()
	{
		// Create a new channels list
		channels = new LinkedList<UniChannel>();
		
		// Have the driver update the channel data
		updateChannels();
		
		// Create a new sensor header
		short numChannels = (short) channels.size();
		UniSensorHeader sensorHeader = new UniSensorHeader((byte)1, vendorID, 
				productID, numChannels, System.currentTimeMillis(), frequency, encryptionFlags);
		
		// Determine size of bytebuffer to allocate
		int sensorHeaderSize = sensorHeader.getPackedSize();
		int channelSize = 0;
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			channelSize += channels.get(channelCount).getPackedSize();
		}
		int sensorPacketSize = sensorHeaderSize + channelSize;
		
		// Allocate the ByteBuffer
		ByteBuffer buffer = ByteBuffer.allocate(sensorPacketSize);
		
		// Pack the sensor packet into the buffer
		sensorHeader.packIntoByteBuffer(buffer);
		int packedSize = sensorHeader.getPackedSize();
		
		// Pack the channels into the buffer
		for (int channelCount = 0; channelCount < numChannels; ++channelCount)
		{
			UniChannel channel = channels.get(channelCount);
			buffer.position(packedSize);
			channel.packIntoByteBuffer(buffer.slice());
			packedSize += channel.getPackedSize();
		}
		
		// Encrypt the buffer
		buffer.rewind();
		ByteBuffer encryptedBuffer = encryptor.encrypt(encryptionFlags, buffer);

		return encryptedBuffer;
	}
	
	/**
	 * Retrieves the sensor packet header for this device.
	 * @return the sensor packet header as a byte array
	 */
	public byte[] getSensorHeader() {
		// Create sensor header with no channels
		UniSensorHeader sensorHeader = new UniSensorHeader((byte)1, vendorID, 
				productID, (short) 0, System.currentTimeMillis(), frequency, encryptionFlags);
		
		// Pack sensor header into ByteBuffer
		ByteBuffer header = ByteBuffer.allocate(sensorHeader.getPackedSize());
		sensorHeader.packIntoByteBuffer(header);
		
		// Create and return byte array
		byte[] headerByteArray = new byte[sensorHeader.getPackedSize()];
		header.rewind();
		header.get(headerByteArray);
		
		return headerByteArray;
	}
	
	/**
	 * Add a UniChannel to the channels list.
	 * @param channel the UniChannel to add to the list
	 */
	protected final void addChannel(UniChannel channel)
	{
		channels.add(channel);
	}
	
	/**
	 * The only function that is necessary for the driver implementors to implement.
	 * Adds each channel to the channels list using addChannel().
	 */
	protected abstract void updateChannels();
	
	private UniCrypt encryptor;
	private LinkedList<UniChannel> channels;
	private short vendorID;
	private short productID;
	private int encryptionFlags;
	private double frequency;
}
