
public class UniElementDescriptor {
	public UniElementDescriptor(boolean signed, boolean integer, boolean sizeUnitBytes, byte size) {
		this.signed = signed;
		this.integer = integer;
		this.sizeUnitBytes = sizeUnitBytes;
		this.size = size;
		byte descriptor = (byte) (this.signed?1:0 << 7);
		descriptor |= (byte) (this.integer?1:0 << 6);
		descriptor |= (byte) (this.sizeUnitBytes?1:0 << 5);
		descriptor |= (byte) (this.size);
		this.descriptor = descriptor;
	}
	
	public UniElementDescriptor(byte descriptor) {
		this.signed = (descriptor >> 7 == 1)?true:false;
		this.integer = (descriptor >> 6 == 1)?true:false;
		this.sizeUnitBytes = (descriptor >> 5 == 1)?true:false;
		this.size = (byte) (descriptor & 0x1F);
		this.descriptor = descriptor;
	}

	public byte getByte() {
		return descriptor;
	}
	
	private boolean signed;
	private boolean integer;
	private boolean sizeUnitBytes;
	private byte size;
	private byte descriptor;
}
