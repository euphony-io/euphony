package euphony.lib.transmitter;

public class EuDataEncoder {
	private String mOriginalSource;
	
	public EuDataEncoder(String _source)
	{
		mOriginalSource = _source;
	}
	
	public String getHexCharSource()
	{
		String retValue = "";
		for(int i = 0; i < mOriginalSource.length(); i++)
		{
			int data = mOriginalSource.charAt(i);
			retValue += Integer.toHexString(data);
		}
		
		return retValue;
	}

	public String getOriginalSource() {
		return mOriginalSource;
	}
	public void setOriginalSource(String mOriginalSource) {
		this.mOriginalSource = mOriginalSource;
	}
}
