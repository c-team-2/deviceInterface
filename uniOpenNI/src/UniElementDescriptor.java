/**
 * Represents a tuple element descriptor.
 * @author Greg Clark
 *
 */
public class UniElementDescriptor {
	public UniElementDescriptor(boolean signed, boolean integer, boolean sizeUnitBytes, byte size) {
		this.signed = signed;
		this.integer = integer;
		this.sizeUnitBytes = sizeUnitBytes;
		this.size = size;
		byte descriptor = (byte) ((this.signed?1:0) << 7);
		descriptor |= (byte) ((this.integer?1:0) << 6);
		descriptor |= (byte) ((this.sizeUnitBytes?1:0) << 5);
		descriptor |= (byte) (this.size);
		this.descriptor = descriptor;
	}
	
	public UniElementDescriptor(byte descriptor) {
		this.signed = ((descriptor & 128) == 128)?true:false;
		this.integer = ((descriptor & 64) == 64)?true:false;
		this.sizeUnitBytes = ((descriptor & 32) == 32)?true:false;
		this.size = (byte) (descriptor & 0x1F);
		this.descriptor = descriptor;
	}

	public boolean isSigned() { return signed; }
	public boolean isInteger() { return integer; }
	public boolean isSizedInBytes() { return sizeUnitBytes; }
	public byte getSize() { return size; }
	public byte getDescriptor() { return descriptor; }
	
	private boolean signed;
	private boolean integer;
	private boolean sizeUnitBytes;
	private byte size;
	private byte descriptor;
}
