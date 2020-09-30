package euphony.lib.transmitter;

import android.util.Log;

import euphony.lib.util.EuOption;

public class EuFreqGenerator {
	
	// FIXED ACOUSTIC DATA
	private final double PI = Math.PI;
	private final double PI2 = PI * 2.0;

	public enum CrossfadeType {
		FRONT, END, BOTH
	}
	
	// Member for Frequency point
	// DEFAULT DEFINITION 
	private short[] mZeroSource;
	private int mBufferSize;
	private double mSampleRate;

	private int mCpIndex;
	private double mCpLastTheta, mCpLastThetaNext;
	private double mCpLastSin, mCpLastCos;
	private int mCpLastFreq;
	private EuOption mTxOption;

	public EuFreqGenerator() {
		mTxOption = new EuOption();
		initialize();
	}

	public EuFreqGenerator(EuOption option) {
		mTxOption = option;
		initialize();
	}

	private void initialize() {
		mBufferSize = mTxOption.getBufferSize();
		mSampleRate = mTxOption.getSampleRate();
		mZeroSource = new short[mBufferSize];

		mCpIndex = 0;
		mCpLastTheta = 0;
		mCpLastSin = 0;
		mCpLastCos = 0;
	}
	
	public short[] makeStaticFrequency(int freq, int degree)
    {
    	double[] double_source = new double[mBufferSize];
    	short[] source = new short[mBufferSize];
        double time, phase;
        
        for(int i = 0; i < mBufferSize; i++)
        {
        	time = (double) i / mSampleRate;
        	double_source[i] = Math.sin(PI2 * (double)freq * time);
        	source[i] = (short)(32767 * double_source[i]);        	
        }
        
        return source;
    }

	private int findCPIndex(int freq) {
		int idx = 0;
		double x = PI2 * (double)freq;
		double v = 0;
		boolean cosChecker = false, sinChecker = false;

		while(cosChecker && sinChecker) {
			v = x * ((double)idx / mSampleRate);

		}
		return idx;
	}

    public short[] makeFrequencyWithCP(int freq) {
		short[] source = new short[mBufferSize];

		double x = PI2 * (double)freq;
		double currentSin = Math.sin( x * ((double) (mCpIndex) / mSampleRate));
		//double thetaDiff = x * ((double)mCpIndex / mSampleRate) - mCpLastTheta;
		double minVal = Math.abs(Math.sin(mCpLastTheta) - currentSin);
		int minValIdx = mCpIndex;
        for(int i = 1; i < mSampleRate; i++) {
            double val = Math.sin(x * ((double)i / mSampleRate));// - mCpLastTheta;
			double cos = Math.cos(x * ((double)i / mSampleRate));// - mCpLastTheta;
            if(minVal > Math.abs(Math.sin(mCpLastTheta) - val)){
            	if(Math.signum(cos) == Math.signum(Math.cos(mCpLastTheta))) {
					minVal = Math.abs(Math.sin(mCpLastTheta) - val);
					minValIdx = i;
				}
            }
        }

        mCpIndex = minValIdx;
		currentSin = Math.sin( x * ((double) (mCpIndex) / mSampleRate));
		double currentSinNext = Math.sin( x * ((double) (mCpIndex + 1) / mSampleRate));
/*
		if(Math.abs(Math.sin(mCpLastThetaNext) - currentSinNext) >= 0.01) {
			int tempIndex = 0;
			double minSinIdx = mCpIndex + 1;
			do {
				tempIndex++;
				currentSinNext = Math.sin(x * ((double) (tempIndex) / mSampleRate) - thetaDiff);


			}while (Math.abs(Math.sin(mCpLastThetaNext) - currentSinNext) >= 0.01);
			mCpIndex = tempIndex;
		}
*/
		Log.i("makeFrequencyWithCP", freq + " : " + " " + mCpIndex);
		Log.i("makeFrequencyWithCP", freq + " : " + " " + Math.sin(mCpLastTheta) + " " + currentSin + " " + (Math.sin(mCpLastTheta) - currentSin));
		//Log.i("makeFrequencyWithCP", freq + " : " + thetaDiff + " " + Math.sin(mCpLastThetaNext)  + " " + currentSinNext + " "  + (Math.sin(mCpLastThetaNext) - currentSinNext));
		// 18000hz / 1sec
		int bufferIdx = 0;
		double theta = 0;
		int i = mCpIndex;
		int bufferSize = mCpIndex + mBufferSize;
		for(; i < bufferSize; i++) {
			theta = x * ((double) i / mSampleRate);
			source[bufferIdx] = (short)(Short.MAX_VALUE * Math.sin(theta));
			bufferIdx++;
		}

		/*
		if(thetaDiff != 0) {
			source[0] *= 0.1;
			source[1] *= 0.5;
		}

		source[bufferIdx - 2] *= 0.5;
		source[bufferIdx - 1] *= 0.1;
*/
		mCpIndex = i;
		mCpLastTheta = x * ((double)mCpIndex / mSampleRate);
		mCpLastThetaNext = x * ((double)(mCpIndex + 1) / mSampleRate);

		return source;
	}
    
    public void euMakeFrequency(short[] source, int freq)
    {
    	source = makeStaticFrequency(freq, 0);
    }
    
    public short[] makeFrequencyWithCrossFade(int freq)
    {
    	return applyCrossFade(makeStaticFrequency(freq, 0), CrossfadeType.BOTH);
    }

	public short[] makeFrequencyWithValue(int value) {
		return applyCrossFade(makeStaticFrequency(mTxOption.getControlPoint() + mTxOption.getDataInterval() * value, 0), CrossfadeType.BOTH);
	}
    
    public short[] applyCrossFade(short[] source, CrossfadeType type)
    {
    	double mini_window;
    	int fade_section = mTxOption.getFadeRange();
		for(int i = 0; i < fade_section; i++)
    	{
    		mini_window = (double)i / (double)fade_section;
    		switch(type) {
				case FRONT:
					source[i] *= mini_window;
					break;
				case END:
					source[source.length - 1 - i] *= mini_window;
					break;
				case BOTH:
					source[i] *= mini_window;
					source[source.length - 1 - i] *= mini_window;
					break;
			}
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
    			sources[i] = applyCrossFade(sources[i], CrossfadeType.BOTH);
    		
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

