package euphony.lib.transmitter;

import euphony.lib.util.EuOption;

public class EuFreqGenerator {
	
	// FIXED ACOUSTIC DATA
	private final double PI = Math.PI;
	private final double PI2 = PI * 2;
	
	// Member for Frequency point
	// DEFAULT DEFINITION 
	private short[] mZeroSource;

	private EuOption mTxOption;

	public EuFreqGenerator() {
		mTxOption = new EuOption();
		mZeroSource = new short[mTxOption.getBufferSize()];
	}

	public EuFreqGenerator(EuOption option) {
		mTxOption = option;
		mZeroSource = new short[mTxOption.getBufferSize()];
	}
	
	public short[] makeStaticFrequency(int freq, int degree)
    {
    	int bufferSize = mTxOption.getBufferSize();
    	double[] double_source = new double[bufferSize];
    	short[] source = new short[bufferSize];
        double time, phase;
        
        for(int i = 0; i < bufferSize; i++)
        {
        	time = (double)i / (double)mTxOption.getSampleRate();
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);        	
        }
        
        return source;
    }
    
    public void euMakeFrequency(short[] source, int freq)
    {
    	source = makeStaticFrequency(freq, 0);
    }
    
    public short[] makeFrequencyWithCrossFade(int freq)
    {
    	return applyCrossFade(makeStaticFrequency(freq, 0));
    }

	public short[] makeFrequencyWithValue(int value) {
		return applyCrossFade(makeStaticFrequency(mTxOption.getControlPoint() + mTxOption.getDataInterval() * value, 0));
	}
    
    public short[] applyCrossFade(short[] source)
    {
    	double mini_window;
    	int fade_section = mTxOption.getFadeRange();
		final int bufferSize = mTxOption.getBufferSize();
    	for(int i = 0; i < fade_section; i++)
    	{
    		mini_window = (double)i / (double)fade_section;
    		source[i] *= mini_window;
    		source[bufferSize-1-i] *= mini_window;
    	}
    	
    	return source;
    }   
    
    public short[] appendRawData(short[] src, short[] objective)
    {
    	int SRC_LENGTH, TOTAL_LENGTH;
    	if(src == null)
    		SRC_LENGTH = 0;
    	else
    		SRC_LENGTH = src.length;
    	TOTAL_LENGTH = SRC_LENGTH + objective.length;
    		
    	short[] dest = new short[TOTAL_LENGTH];

    	for(int i = 0; i < SRC_LENGTH; i++)
    		dest[i] = src[i];
    	for(int i = SRC_LENGTH; i < TOTAL_LENGTH; i++)
    		dest[i] = objective[i - SRC_LENGTH];
    	
    	return dest;
    }
    
    public short[] euLinkRawData(short[]... sources)
    {
		final int bufferSize = mTxOption.getBufferSize();
    	short[] dest = new short[sources.length * bufferSize];
    	
    	for(int i = 0; i < sources.length; i++)
    		for(int j = 0; j < sources[i].length; j++)
    			dest[j + i * bufferSize] = sources[i][j];
    	
    	return dest;
    }
    
    public short[] euLinkRawData(boolean isCrossfaded, short[]... sources)
    {
		final int bufferSize = mTxOption.getBufferSize();
    	short[] dest = new short[sources.length * bufferSize];
    	for(int i = 0; i < sources.length; i++)
    	{
    		if(isCrossfaded)
    			sources[i] = applyCrossFade(sources[i]);
    		
    		for(int j = 0; j < sources[i].length; j++)
    				dest[j + i * bufferSize] = sources[i][j];
    	}
    	
    	return dest;
    }
    
    public short[] mixingRawData(short[]... sources)
    {
    	short[] dest = sources[0].clone();
    	
    	for(int i = 1; i < sources.length; i++)
    	{
    		for(int j = 0; j < sources[i].length; j++){
    			dest[j] = (short) (((int)dest[j] + (int)sources[i][j])/2);
    		}
    	}
		return dest;
    }
    
    public short[] euMakeMaximumVolume(short[] source)
	 {
	    	int max = 0;
	    	//SCAN FOR VOLUME UP
		 	for (short i1 : source)
		 		if (max < Math.abs(i1))
		 			max = i1;
	    	if(32767 == max) 
	    		return source;
	    	
	    	for(int i = 0; i < source.length; i++)
	    		source[i] *= 32767/max;
	    	
	    	return source;
	 }
    
	public short[] getZeroSource() {
		return mZeroSource;
	}
}

