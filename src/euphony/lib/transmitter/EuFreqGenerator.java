package euphony.lib.transmitter;

public class EuFreqGenerator {
	
	// FIXED ACOUSTIC DATA
	public final int SAMPLERATE = 44100;
	public final int DATA_LENGTH = 4096;
	public final double PI = Math.PI;
	public final double PI2 = PI * 2;
	
	// Member for Frequency point
	// DEFAULT DEFINITION 
	private int mFreqBasePoint = 18000;
	private int mFreqSpan = 100;
	private short[] mZeroSource = new short[DATA_LENGTH];

	public EuFreqGenerator() { }
	
	public EuFreqGenerator(int freqStartPoint, int freqSpan)
	{
		mFreqSpan = freqSpan;
		mFreqBasePoint = freqStartPoint;
	}
	
	public short[] euMakeFrequency(int freq)
    {
    	double[] double_source = new double[DATA_LENGTH];
    	short[] source = new short[DATA_LENGTH];
        float rate = SAMPLERATE;
        double time;
        for(int i = 0 ; i < DATA_LENGTH; i++)
        {
        	time = i / rate;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);
        }
        
        return source;
    }
    
    public void euMakeFrequency(short[] source, int freq)
    {
    	double[] double_source = new double[DATA_LENGTH];
        float rate = SAMPLERATE;
        double time;
        for(int i = 0 ; i < DATA_LENGTH; i++)
        {
        	time = i / rate;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);
        }
    }
    
	//updated
    private short[] euMakeFrequencyWithCrossFade(int freq)
    {
    	double fade_window;
    	int fade_section = DATA_LENGTH / 16;
    	double[] double_source = new double[DATA_LENGTH];
    	short[] source = new short[DATA_LENGTH];
        float rate = SAMPLERATE;
        double time;
        
        //APPLYING CROSS FADE!
        for(int i = 0 ; i < fade_section; i++)
        {
        	time = (double)i / (double)rate;
        	fade_window = (double)i / (double)fade_section;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(double_source[i] * 32767 * fade_window);
        	source[DATA_LENGTH - 1 - i] = (short)(double_source[i] * 32767 * fade_window);
        }
        for(int i = fade_section; i < DATA_LENGTH - fade_section; i++)
        {
        	time = (double)i / (double)rate;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);
        }
        
        return source;
    }
    
    public short[] euApplyCrossFade(short[] source)
    {
    	double mini_window;
    	int fade_section = DATA_LENGTH / 8;
    	for(int i = 0; i < fade_section; i++)
    	{
    		mini_window = (double)i / (double)fade_section;
    		source[i] *= mini_window;
    		source[DATA_LENGTH-1-i] *= mini_window;
    	}
    	
    	return source;
    }   
    
    public short[] euAppendRawData(short[] src, short[] objective)
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
    
    public short[] euMixingRawData(short[]... sources)
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
	    	for(int i = 0; i < source.length; i++)
	    		if(max < Math.abs(source[i])) 
	    			max = source[i];
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

