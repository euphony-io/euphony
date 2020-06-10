package euphony.lib.receiver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.function.Function;

import android.util.Log;

import euphony.lib.util.EuOption;
import euphony.lib.util.PacketErrorDetector;

public class EuFreqObject {

	protected EuOption mRxOption;
	public int[] DATA_FREQ;
	public int[] DATA_FREQ_INDEX_FOR_FFT;

	private Boolean isStarted = false;
	private Boolean isCompleted = false;

	public Boolean getStarted() {
		return isStarted;
	}
	public void setStarted(Boolean started) {
		isStarted = started;
	}
	public Boolean getCompleted() {
		return isCompleted;
	}
	public void setCompleted(Boolean completed) {
		isCompleted = completed;
	}

	private Boolean isRecording;

	private ByteBuffer samples;
	private FloatBuffer spectrum;
	private FloatBuffer spectrum_p;
	public static String receiveStr = "";
	private String mReceivedData;

	public String getReceivedData() {
		return mReceivedData;
	}

	public void setReceivedData(String _receivedData) {
		this.mReceivedData = _receivedData;
	}

	private int[] mFreqArray;
	private int[] mTempRef;
	private int[] mRefCntIndexArray;
	private int[] mDynamicRefArray;

	private ArrayList<Integer> mChannelArrayList = new ArrayList<Integer>();

	private AudioRecorder mRecorder;
	private KissFFT FFT;

	private int []mArrMaxIndex;
	private int []mArrSampleIndex;
	private int []mArrSampleTemp;
	private int []mArrChannelTemp;
	public int []mArrcntCheck;

	public EuFreqObject()
	{
		mRxOption = new EuOption();
		initEuFreqObject(mRxOption);
	}

	public EuFreqObject(EuOption mOption)
	{
		mRxOption = mOption;
		initEuFreqObject(mOption);
	}

	private void initEuFreqObject(EuOption mOption)
	{
		mRecorder = new AudioRecorder(mOption.getSampleRate());

		int dataRate = mOption.getDataRate();
		int dataRatePlus1 = dataRate + 1;
		int fftSize = mOption.getFFTSize();
		mFreqArray = new int[dataRatePlus1];
		mTempRef = new int[dataRatePlus1];
		mRefCntIndexArray = new int[dataRatePlus1];
		mDynamicRefArray = new int[dataRatePlus1];
		DATA_FREQ = new int[dataRate];
		DATA_FREQ_INDEX_FOR_FFT = new int[dataRatePlus1];
		for(int i = 0; i < dataRate; i++) {
			DATA_FREQ[i] = mOption.getControlPoint() + mOption.getDataInterval() * i;
			DATA_FREQ_INDEX_FOR_FFT[i] = ((int)((DATA_FREQ[i] / 22050.0) * mOption.getFFTSize() / 2)) + 1;
			mDynamicRefArray[i] = mOption.getDefaultReference();
		}
		DATA_FREQ_INDEX_FOR_FFT[dataRate] = ((int)((mOption.getOutsetFrequency() / 22050.0) * mOption.getFFTSize() / 2)) + 1;
		mDynamicRefArray[dataRate] = mOption.getDefaultReference();

		samples = allocateByteBuffer(fftSize);
		spectrum = allocateFloatBuffer(fftSize / 2 + 1);
		spectrum_p = allocateFloatBuffer(fftSize / 2 + 1);

		FFT = new KissFFT(mOption.getFFTSize());

		mArrMaxIndex = new int[dataRatePlus1];
		mArrSampleIndex= new int[dataRatePlus1];
		mArrSampleTemp = new int[dataRatePlus1];
		mArrChannelTemp = new int[dataRatePlus1];
		mArrcntCheck = new int[dataRatePlus1];

		isRecording = false;
	}

	public void processFFT()
	{
		if(!isRecording){
			mRecorder.start();
			isRecording = true;
		}
		mRecorder.read(samples, mRxOption.getFFTSize());
		FFT.doSpectrums(samples, spectrum);
		//FFT.spectrum_for_phase(samples, spectrum_p);
		//FFT.getRealPart(real);
		//FFT.getImagPart(image);
	}

	public void processFFT(EuWindows.Windows mWindow)
	{
		if(!isRecording){
			mRecorder.start();
			isRecording = true;
		}
		mRecorder.read(samples, mRxOption.getFFTSize(), mWindow);
		FFT.doSpectrums(samples, spectrum);
	}

	public void destroyFFT()
	{
		FFT.dispose();
		mRecorder.stop();
		isRecording = false;
	}

	public int detectFreq(int fFrequency){

		double fFreqRatio;
		int freqIndex;
		float fmax;

		fFreqRatio = fFrequency / 22050.0;
		freqIndex = ( (int) (fFreqRatio * mRxOption.getFFTSize() / 2) ) + 1;

		//f1 = spectrum.get(freqIndex-1);
		fmax = spectrum.get(freqIndex);
		//f3 = spectrum_p.get(freqIndex);
		//r1 = real.get(freqIndex);
		//i1 = image.get(freqIndex);
		/*
		String s = "";
		for(int i = freqIndex - 1; i <= freqIndex + 1; i++)
			s += " " + spectrum.get(i);
		Log.i("HELLO FREQ", freqIndex + "::" + s);
		*/
		//f3 = spectrum.get(freqIndex+1);

		//if( fmax < f2 )	fmax = f2;
		//if( fmax < f3 )	fmax = f3;

		return (int)(fmax*100000);
	}

	public float getSpectrumValue(int idx) {
		return spectrum.get(idx);
	}

	public int detectFreqByIdx(int idx) {
		float fmax = spectrum.get(DATA_FREQ_INDEX_FOR_FFT[idx]);
		return (int) (fmax * 100000);
	}

	public int detectFreqByIdx(int idx, int offset_idx) {
		float fmax = spectrum.get(DATA_FREQ_INDEX_FOR_FFT[idx] + offset_idx);
		return (int) (fmax * 100000);
	}

	private int mMaxIndex = -1;		
	private int mSampleIndex = 0;
	private int mSampleTemp = 0;
	private int mChannelTemp = 0;
	public int cntCheck = 0;

	public void catchSingleData()
	{
		int currentFreq;
		int dataRate = mRxOption.getDataRate();
		// UPDATED ON 1/2/2014
		// START BIT's Frequency Detection
		mSampleTemp = detectFreqByIdx(dataRate);
		mMaxIndex = dataRate;
		// START BIT's Dynamic Reference Catch
		mDynamicRefArray[dataRate] = getDynamicReference(mSampleTemp, dataRate);

		// Rest of frequency processing
		for(int i = 0; i < dataRate; i++)
		{
			currentFreq = detectFreqByIdx(i);
			mDynamicRefArray[i] = getDynamicReference(currentFreq, i);
			
			if(currentFreq >= mSampleTemp && currentFreq >= mDynamicRefArray[i]){
				mSampleTemp = currentFreq;
				mMaxIndex = i;
			}
		}

		if(mMaxIndex != -1){
			mFreqArray[mMaxIndex]++;
		}
		if(mSampleIndex < 7)
		{  
			mSampleIndex++;
		}
		else
		{ 
			for(int i = 0; i < dataRate + 1 ; i++)
			{
				if(mFreqArray[i]>mChannelTemp)
				{
					mChannelTemp = mFreqArray[i];
					mMaxIndex = i;
				}
				//mbFreqArray[i] = 0;
			}

			if(mChannelTemp > 2 && mMaxIndex != -1)
			{
				if(mMaxIndex == 16){
					if(mChannelArrayList.size() > 1){
						cntCheck++;
						Log.v("mChanelArrayList.size()","mChanelArrayList.size():" +mChannelArrayList.size());
						
						mReceivedData = "";
						int []a= new int [mChannelArrayList.size()- 2];
						for(int i = 0 ; i< mChannelArrayList.size() - 2; i++){
							a[i]=mChannelArrayList.get(i);
							if(a[i] > 9)
								mReceivedData += "" + (char)('a' + (a[i] - 10));
							else
								mReceivedData += "" + a[i];		
						}
						
						if(PacketErrorDetector.makeCheckSum(a) == mChannelArrayList.get(mChannelArrayList.size()-2) && (PacketErrorDetector.makeParallelParity(a) == mChannelArrayList.get(mChannelArrayList.size()-1))) {
							isStarted = false;
							isCompleted = true;
							receiveStr = EuDataDecoder.decodeStaticHexCharSource(a);
						}
						mChannelArrayList.clear();
					}
				}
				else{
					mChannelArrayList.add(mMaxIndex);
				}
				Log.v("DATA","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"  tempxcnt :" + mChannelTemp +"   Data :"+ (int) this.detectFreqByIdx(mMaxIndex));
				Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
						+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]+" 16:"+mFreqArray[16]);
			}
			else 
			{
				mChannelArrayList.add(-1);
				Log.v("DATAX","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"   Data :"+ (int) this.detectFreqByIdx(mMaxIndex));
				Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
						+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]+" 16:"+mFreqArray[16]);

			}
			mMaxIndex = -1;
			mSampleIndex = 0 ;
			mSampleTemp = 0;
			mChannelTemp = 0;
			for(int i = 0; i < dataRate + 1 ; i++){////
				mFreqArray[i] = 0;////
			}/////
		}

	}
	public void catchMultiData()
	{
		for(int j = 0; j <= mRxOption.getDataRate(); j++)
		{ 
			mArrMaxIndex[j] = -1;
			mArrSampleIndex[j] = 0;
			mArrSampleTemp[j] = 0;
			mArrChannelTemp[j] = 0;
			mArrcntCheck[j] = 0;
			mFreqArray[j] = 0;
		}
		int []arrCurrentFreq = new int[mRxOption.getDataRate() + 1];
		for(int i = 0; i <= mRxOption.getDataRate() ; i++)
		{ 
			arrCurrentFreq[i] = this.detectFreq(mRxOption.getControlPoint() + mRxOption.getDataInterval()*i);
			if(arrCurrentFreq[i] >=200){
				mArrMaxIndex[i] = i;
			}
		}
	}	

	public int getMaxIndex() {
		return mMaxIndex;
	}
	public void setMaxIndex(int mMaxIndex) {
		this.mMaxIndex = mMaxIndex;
	}

	public int[] getArrMaxIndex() {
		return mArrMaxIndex;
	}
	public void setArrMaxIndex(int[] mArrMaxIndex) {
		this.mArrMaxIndex = mArrMaxIndex;
	}

	public int mStartSampleCnt = 0;
	public Boolean checkStartPoint()
	   {
	      int tempFreq = 50;
	      int tempIndex = -1;
	      int currentFreq;
	      for(int i = -1; i < 2; i++)
	      {
	         currentFreq = (int) this.detectFreqByIdx(mRxOption.getDataRate(), i);
	         if(currentFreq > tempFreq)
	         {
	            tempFreq = currentFreq;
	            tempIndex = 0 ;
	         }
	      }
	      if(tempIndex == 0){   
	         if(mStartSampleCnt==8)
	            return true;
	         else
	            mStartSampleCnt++;
	      }
	      else   
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
			this.processFFT();

			int curFreq = this.detectFreq(_freq);
			dynRef = euGetDynamicRef(curFreq, dynRef, preservedRef, dynRefCnt);

			if(curFreq >= dynRef){
				int prevFreq = this.detectFreq(_freq - mRxOption.getDataInterval());
				int nextFreq = this.detectFreq(_freq + mRxOption.getDataInterval());

				if(prevFreq > curFreq || nextFreq > curFreq)
					continue;
				else{
					criticalCnt++;
					temp = limitCnt;
				}
			}

			if(criticalCnt > 4)
			{		
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
		if(dynRef > mRxOption.getMaxReference())
			dynRef = mRxOption.getMaxReference();
		if(dynRef < mRxOption.getMinReference())
			dynRef = mRxOption.getMinReference();

		return dynRef;
	}

	public int getDynamicReference(int nfreq, int freqIndex)
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
		if(mDynamicRefArray[freqIndex] > mRxOption.getMaxReference())
			mDynamicRefArray[freqIndex] = mRxOption.getMaxReference();
		if(mDynamicRefArray[freqIndex] < mRxOption.getMinReference())
			mDynamicRefArray[freqIndex] = mRxOption.getMinReference();

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
	
	private ShortBuffer allocateShortBuffer(int numSamples) {
		ByteBuffer buffer = allocateByteBuffer(numSamples * 2);
		return buffer.asShortBuffer();
	}

	public EuOption getRxOption() {
		return mRxOption;
	}

	public void setRxOption(EuOption option) {
		this.mRxOption = option;
	}
}
