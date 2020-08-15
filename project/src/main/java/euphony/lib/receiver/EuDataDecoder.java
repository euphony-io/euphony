package euphony.lib.receiver;

public class EuDataDecoder {
	private String mOriginalSource;

	public EuDataDecoder(String _source)
	{
		mOriginalSource = _source;
	}
	
	public String decodeHexCharSource()
	{
		return decodeStaticHexCharSource(mOriginalSource);
	}
	
	public static String decodeStaticHexCharSource(String _source)
	{
		String retValue = "";
		for(int i = 0; i < _source.length()-1; i+=2){
			int data = Integer.parseInt("" + _source.charAt(i) + _source.charAt(i+1), 16);
			retValue += (char)data;
		}
		
		return retValue;
	}
	
	public static String decodeStaticHexCharSource(int[] _intArray)
	{
		String retValue = "";
		for(int i = 0; i < _intArray.length-1; i+=2)
			retValue += (char)(_intArray[i]*16 + _intArray[i+1]);
		
		return retValue;
	}
	
	public String getOriginalSource() {
		return mOriginalSource;
	}

	public void setOriginalSource(String mOriginalSource) {
		this.mOriginalSource = mOriginalSource;
	}
}
