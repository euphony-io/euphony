package euphony.lib.receiver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.util.Log;

public class EuFreqObject {
	
	public final int SAMPLERATE = 44100;
	public final int fftsize = 1024;
	public final int MAXREFERENCE = 4000;
	public final int MINREFERENCE = 50;
	public final double MAXFREQUENCY = 22050.0;
	public final int DEFAULT_REF = 500;
	public final int START_FREQ = 18000;
	public final int RXCHANEL = 16;
	private final int STARTCHANNEL = 1;	
	private int mFreqSpan = 100;
	
	private ByteBuffer samples = allocateByteBuffer(fftsize);
	private FloatBuffer spectrum = allocateFloatBuffer(fftsize/2+1);
	
	private int[] mbFreqArray = new int[RXCHANEL + STARTCHANNEL];
	private int[] mTempRef = new int[RXCHANEL + STARTCHANNEL];
	private int[] mRefCntIndexArray = new int[RXCHANEL + STARTCHANNEL];
	private int[] mDynamicRefArray = new int[RXCHANEL + STARTCHANNEL];
	
	private ArrayList<Integer> mChanelArrayList = new ArrayList<Integer>();
	
	private AudioRecorder recorder = new AudioRecorder(SAMPLERATE, fftsize * 2);
	private KissFFT FFT = new KissFFT(fftsize);
	
	public EuFreqObject()
	{
		//INIT DYNAMIC REFERENCE ARRAY..
		for (int i = 0; i < RXCHANEL; i++)
			mDynamicRefArray[i] = DEFAULT_REF;
	}
	
	
	public void StartFFT()
	{
		recorder.start();
		recorder.read(samples, fftsize);
		FFT.spectrum(samples, spectrum);
	}
	
	public void DestroyFFT()
	{
		FFT.dispose();
		recorder.destroy();
		recorder = null;
	}
	
	public int euDetectFreq(int fFrequency){

		double fFreqRatio;
		int freqIndex;
		float f1, f2, f3,fmax;
		
		fFreqRatio = fFrequency / MAXFREQUENCY;		
		freqIndex = ( (int) (fFreqRatio * fftsize / 2) ) + 1;
		
		f1 = spectrum.get(freqIndex-1);
		f2 = spectrum.get(freqIndex);
		f3 = spectrum.get(freqIndex+1);
		
		fmax = f1;
		if( fmax < f2 )	fmax = f2;
		if( fmax < f3 )	fmax = f3;
		
		return (int)(fmax*100000);
	}

	private int mMaxIndex = -1;	
	private int mSampleIndex = 0;
	private int mSampleTemp = 0;
	private int mChannelTemp = 0;
	
	public void euCatchData() 
	{
		int currentFreq = 0;
		for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++)
		{ 
			currentFreq = (int) this.euDetectFreq(START_FREQ + mFreqSpan*i);
			mDynamicRefArray[i] = euGetDynamicReference(currentFreq, i);			
			
			if(currentFreq>=mSampleTemp && currentFreq >= mDynamicRefArray[i]){
				mSampleTemp = currentFreq;
				mMaxIndex = i;
			}
		}
		if(mMaxIndex != -1)
			mbFreqArray[mMaxIndex]++;
		if(mSampleIndex < 7)
		{  
			mSampleIndex++;
		}
		else
		{ 
			for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++)
			{
				if(mbFreqArray[i]>mChannelTemp)
				{
					mChannelTemp = mbFreqArray[i];
					mMaxIndex = i;
				}
				mbFreqArray[i] = 0;
			}

			if(mSampleTemp > 2 && mMaxIndex != -1)
			{
				mChanelArrayList.add(mMaxIndex);
				Log.v("DATA","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"  tempcnt :" + mSampleTemp +"  tempxcnt :" + mChannelTemp +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
			}
			else 
			{
				mChanelArrayList.add(-1);
				Log.v("DATAX","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"  tempcnt :" +mSampleTemp +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
			}
			mMaxIndex = -1;
			mSampleIndex = 0 ;
			mSampleTemp = 0;
			mChannelTemp = 0;

		}
	}
	private int mStartSampleCnt = 0;
	public Boolean euCheckStartPoint()
	{
		int tempFreq = 50;
		int tempIndex = -1;
		int currentFreq = 0;
		for(int i = -1; i < 2; i++)
		{
			currentFreq = (int) this.euDetectFreq(START_FREQ + mFreqSpan*i);
			if(currentFreq > tempFreq)
			{
				tempFreq = currentFreq;
				tempIndex = i ;
			}
		}
		if(tempIndex == 0){	// 18000?´ë©´ ì¦ê?
			if(mStartSampleCnt==8)
				return true;
			else
				mStartSampleCnt++;
		}
		else	// 18000???„ë‹Œ ê²½ìš°
			mStartSampleCnt = 0;
		
		return false;
	}
	
	private final int SENSOR_LIMIT_COUNT = 2000;
	private	Boolean euSpecificFreqSensor(int _freq)
	{
		int dynRef = 500;
		int dynRefCnt = 0;
		int criticalCnt = 0;
		int limitCnt = 0;
		int temp = 0;
		int preservedRef = 0;
		while(SENSOR_LIMIT_COUNT > limitCnt++)
		{
			this.StartFFT();
			
			int curFreq = this.euDetectFreq(_freq);
			dynRef = euGetDynamicRef(curFreq, dynRef, preservedRef, dynRefCnt);
			
			if(curFreq >= dynRef){
				int prevFreq = this.euDetectFreq(_freq - mFreqSpan);
				int nextFreq = this.euDetectFreq(_freq + mFreqSpan);
				
				if(prevFreq > curFreq || nextFreq > curFreq)
					continue;
				else{
					criticalCnt++;
					temp = limitCnt;
				}
			}

			//4 ?´ìƒ??ê²½ìš° ?°ì´?°ë¡œ ?¸ì •
			if(criticalCnt > 4)
			{
				// ?°ì´?°ê? ?°ì†?ì¸ì§?ì²´í¬
				if(limitCnt - temp > 12)
				{
					criticalCnt = 0;
					continue;
				}
				else
					return true;
			}
		}
		return false;			
	}	
	
	public int euGetDynamicRef(int curFreq, int dynRef, int preservedRef, int dynRefCnt)
	{
		if( curFreq > dynRef) // DATA TRUE
		{
			switch(dynRefCnt)
			{
			case 0:
				dynRef = curFreq;
				dynRefCnt++;
				break;
			case 1:
				dynRef = (curFreq + dynRef)/3;
				preservedRef = dynRef;
				dynRefCnt++;
				break;
			case 2:
				dynRef = preservedRef;
				break;
			}	
		}
		else //DATA FALSE
		{
			dynRef -= ((dynRef-curFreq)*0.9);
			dynRefCnt = 0;
		}
		
		// MAXIMUM and MIMINUM DATA CATCHING
		if(dynRef > MAXREFERENCE) 
			dynRef = MAXREFERENCE;  				
		if(dynRef < MINREFERENCE)	
			dynRef = MINREFERENCE;
		
		return dynRef;
	}
	
	public int euGetDynamicReference(int nfreq, int freqIndex)
	{	
		if( nfreq > mDynamicRefArray[freqIndex]) // DATA TRUE
		{
			switch(mRefCntIndexArray[freqIndex])
			{
			case 0:
				mDynamicRefArray[freqIndex] = nfreq;
				mRefCntIndexArray[freqIndex]++;
				break;
			case 1:
				mDynamicRefArray[freqIndex] = (nfreq + mDynamicRefArray[freqIndex])/2;
				mTempRef[freqIndex] = mDynamicRefArray[freqIndex];
				mRefCntIndexArray[freqIndex]++;
				break;
			case 2:
				mDynamicRefArray[freqIndex] = mTempRef[freqIndex];
				break;
			}	
		}
		else //DATA FALSE
		{
			mDynamicRefArray[freqIndex] -= ((mDynamicRefArray[freqIndex]-nfreq)*0.9);
			mRefCntIndexArray[freqIndex] = 0;
		}
		
		// MAXIMUM and MIMINUM DATA CATCHING
		if(mDynamicRefArray[freqIndex] > MAXREFERENCE) 
			mDynamicRefArray[freqIndex] = MAXREFERENCE;  				
		if(mDynamicRefArray[freqIndex] < MINREFERENCE)	
			mDynamicRefArray[freqIndex] = MINREFERENCE;
		
		return mDynamicRefArray[freqIndex];
	}
	

	private ByteBuffer allocateByteBuffer(int numSamples) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(numSamples * 2);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}
	
	private FloatBuffer allocateFloatBuffer(int numSamples) {
		ByteBuffer buffer = allocateByteBuffer(numSamples * 2);
		return buffer.asFloatBuffer();
	}

}
