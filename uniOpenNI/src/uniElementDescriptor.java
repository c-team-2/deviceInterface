
public class uniElementDescriptor {
	public uniElementDescriptor(Boolean signed, Boolean integer, Boolean sizeUnitBytes, byte size) {
		this.signed = (byte)(signed?1:0);
		this.integer = (byte)(integer?1:0);;
		this.sizeUnitBytes = (byte)(sizeUnitBytes?1:0);
		this.size = size;
		byte descriptor = (byte) (this.signed << 7);
		descriptor |= (this.integer << 6);
		descriptor |= (this.sizeUnitBytes << 5);
		descriptor |= (this.size);
		this.descriptor = descriptor;
	}
	
	public byte getByte() {
		return descriptor;
	}
	
	byte signed;
	byte integer;
	byte sizeUnitBytes;
	byte size;
	byte descriptor;
}
