package co.euphony.tx;

import co.euphony.common.Constants;

public class EuFreqGenerator {
	
	// FIXED ACOUSTIC DATA
	public final int SAMPLERATE = Constants.SAMPLERATE;//44100;
	public final int DATA_LENGTH = Constants.DATA_LENGTH;//2048;
	public final double PI = Math.PI;
	public final double PI2 = PI * 2;
	
	// Member for Frequency point
	// DEFAULT DEFINITION 
	private int mFreqBasePoint = Constants.START_FREQ;
	private int mFreqSpan = Constants.CHANNEL_SPAN;	//86
	private short[] mZeroSource = new short[DATA_LENGTH];

	public EuFreqGenerator() { }
	
	public EuFreqGenerator(int freqStartPoint, int freqSpan)
	{
		mFreqSpan = freqSpan;
		mFreqBasePoint = freqStartPoint;
	}
	
	public short[] makeStaticFrequency(int freq, int degree)
    {
    	double[] double_source = new double[DATA_LENGTH];
    	short[] source = new short[DATA_LENGTH];
        double time, phase;
        
        for(int i = 0; i < DATA_LENGTH; i++)
        {
        	time = (double)i / (double)SAMPLERATE;
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
		return applyCrossFade(makeStaticFrequency(getFreqBasePoint() + getFreqSpan() * value, 0));
	}
    
    public short[] applyCrossFade(short[] source)
    {
    	double mini_window;
    	int fade_section = Constants.FADE_RANGE;
    	for(int i = 0; i < fade_section; i++)
    	{
    		mini_window = (double)i / (double)fade_section;
    		source[i] *= mini_window;
    		source[DATA_LENGTH-1-i] *= mini_window;
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
    	short[] dest = new short[sources.length * DATA_LENGTH];
    	
    	for(int i = 0; i < sources.length; i++)
    		for(int j = 0; j < sources[i].length; j++)
    			dest[j + i * DATA_LENGTH] = sources[i][j];
    	
    	return dest;
    }
    
    public short[] euLinkRawData(boolean isCrossfaded, short[]... sources)
    {
    	short[] dest = new short[sources.length * DATA_LENGTH];
    	for(int i = 0; i < sources.length; i++)
    	{
    		if(isCrossfaded)
    			sources[i] = applyCrossFade(sources[i]);
    		
    		for(int j = 0; j < sources[i].length; j++)
    				dest[j + i * DATA_LENGTH] = sources[i][j];		    				
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
	
	public int getFreqBasePoint() {
		return mFreqBasePoint;
	}

	public void setFreqBasePoint(int mFreqBasePoint) {
		this.mFreqBasePoint = mFreqBasePoint;
	}
	
	public int getFreqSpan() {
		return mFreqSpan;
	}

	public void setFreqSpan(int mFreqSpan) {
		this.mFreqSpan = mFreqSpan;
	}

    
}

