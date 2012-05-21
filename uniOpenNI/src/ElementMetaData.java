
public class ElementMetaData {
	enum UniType {
		int8,
		int16,
		int32,
		int64,
		float32,
		float64,
		nonprimitive;
	}
	
	UniType type;
	int size;
	int bufferIndex;
}