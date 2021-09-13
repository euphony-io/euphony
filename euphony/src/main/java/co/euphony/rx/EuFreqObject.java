package co.euphony.rx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import android.util.Log;
import co.euphony.common.Constants;
import co.euphony.util.PacketErrorDetector;

public class EuFreqObject {

	private final int STARTCHANNEL = 1;
	public final int START_BIT_IDX = Constants.CHANNEL;

	public int[] DATA_FREQ = new int[Constants.CHANNEL];
	public int[] DATA_FREQ_INDEX_FOR_FFT = new int[Constants.CHANNEL + 1];

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

	private Boolean isRecording = false;

	private ByteBuffer samples = allocateByteBuffer(Constants.FFT_SIZE);
	private FloatBuffer spectrum = allocateFloatBuffer(Constants.FFT_SIZE / 2 + 1);
	private FloatBuffer spectrum_p = allocateFloatBuffer(Constants.FFT_SIZE / 2 + 1);
	private String mReceivedData;

	private int[] mFreqArray = new int[Constants.CHANNEL + STARTCHANNEL];
	private int[] mTempRef = new int[Constants.CHANNEL + STARTCHANNEL];
	private int[] mRefCntIndexArray = new int[Constants.CHANNEL + STARTCHANNEL];
	private int[] mDynamicRefArray = new int[Constants.CHANNEL + STARTCHANNEL];

	private ArrayList<Integer> mChannelArrayList = new ArrayList<>();

	private AudioRecorder recorder;
	private KissFFT FFT = new KissFFT(Constants.FFT_SIZE);

	private int []mArrMaxIndex = new int [Constants.CHANNEL + STARTCHANNEL];
	private int []mArrSampleIndex = new int [Constants.CHANNEL + STARTCHANNEL];
	private int []mArrSampleTemp = new int [Constants.CHANNEL + STARTCHANNEL];
	private int []mArrChannelTemp = new int [Constants.CHANNEL + STARTCHANNEL];
	public int []mArrcntCheck = new int [Constants.CHANNEL + STARTCHANNEL];

	public EuFreqObject()
	{
		recorder = new AudioRecorder(Constants.SAMPLERATE);
		//INIT DYNAMIC REFERENCE ARRAY..
		for (int i = 0; i < Constants.CHANNEL; i++) {
			DATA_FREQ[i] = Constants.STANDARD_FREQ + Constants.CHANNEL_INTERVAL * i;
			DATA_FREQ_INDEX_FOR_FFT[i] = Math.round((DATA_FREQ[i] / (float)Constants.HALF_SAMPLERATE) * (Constants.FFT_SIZE >> 1));
			mDynamicRefArray[i] = Constants.DEFAULT_REF;
		}
		DATA_FREQ_INDEX_FOR_FFT[Constants.CHANNEL] = Math.round((Constants.START_SIGNAL_FREQ / (float)Constants.HALF_SAMPLERATE) * (Constants.FFT_SIZE >> 1));
		mDynamicRefArray[Constants.CHANNEL] = Constants.DEFAULT_REF;

		isRecording = false;
	}


	public void processFFT()
	{
		if(!isRecording){
			recorder.start();
			isRecording = true;
		}
		recorder.read(samples, Constants.FFT_SIZE);
		FFT.doSpectrums(samples, spectrum);
	}

	public void processFFT(short windowsNum)
	{
		if(!isRecording){
			recorder.start();
			isRecording = true;
		}
		recorder.read(samples, Constants.FFT_SIZE, windowsNum);
		FFT.doSpectrums(samples, spectrum);
	}

	public void destroyFFT()
	{
		FFT.dispose();
		recorder.stop();
		isRecording = false;
	}

	public int detectFreq(int fFrequency){
		final float fFreqRatio = fFrequency / (float) Constants.HALF_SAMPLERATE;
		final int freqIndex = Math.round(fFreqRatio * (Constants.FFT_SIZE >> 1));
		final float fmax = spectrum.get(freqIndex);

		return (int)(fmax*100000);
	}

	public String getReceivedData() {
		return mReceivedData;
	}

	public void setReceivedData(String _receivedData) {
		this.mReceivedData = _receivedData;
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
		int currentFreq = 0;
		// UPDATED ON 1/2/2014
		// START BIT's Frequency Detection
		mSampleTemp = detectFreqByIdx(Constants.CHANNEL);
		mMaxIndex = Constants.CHANNEL;
		// START BIT's Dynamic Reference Catch
		mDynamicRefArray[Constants.CHANNEL] = getDynamicReference(mSampleTemp, Constants.CHANNEL);

		// Rest of frequency processing
		for(int i = 0; i < Constants.CHANNEL; i++)
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
			for(int i = 0; i < Constants.CHANNEL + STARTCHANNEL; i++)
			{
				if(mFreqArray[i] > mChannelTemp)
				{
					mChannelTemp = mFreqArray[i];
					mMaxIndex = i;
				}
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
						} else {
							Log.v("DATA","Parity Error Check = " + PacketErrorDetector.makeParallelParity(a));
							Log.v("DATA","Checksum Error Check = " + PacketErrorDetector.makeCheckSum(a));
							Log.v("DATA","ReceivedData = " + mReceivedData);
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
			for(int i = 0; i < Constants.CHANNEL + STARTCHANNEL ; i++){
				mFreqArray[i] = 0;
			}
		}

	}

	public void catchMultiData()
	{
		for(int j = 0; j < Constants.CHANNEL + STARTCHANNEL ; j++)
		{ 
			mArrMaxIndex[j] = -1;
			mArrSampleIndex[j] = 0;
			mArrSampleTemp[j] = 0;
			mArrChannelTemp[j] = 0;
			mArrcntCheck[j] = 0;
			mFreqArray[j] = 0;
		}

		int []arrCurrentFreq = new int[Constants.CHANNEL + STARTCHANNEL];
		for(int i = 0; i < Constants.CHANNEL + STARTCHANNEL ; i++)
		{ 
			arrCurrentFreq[i] = (int) this.detectFreq(Constants.STANDARD_FREQ + Constants.CHANNEL_INTERVAL * i);
			if(arrCurrentFreq[i] >=200){
				//mArrSampleTemp[i] = arrCurrentFreq[i];
				mArrMaxIndex[i] = i;
			}
		}
	}	

	public int getmMaxIndex() {
		return mMaxIndex;
	}
	public void setmMaxIndex(int mMaxIndex) {
		this.mMaxIndex = mMaxIndex;
	}

	public int[] getmArrMaxIndex() {
		return mArrMaxIndex;
	}
	public void setmArrMaxIndex(int[] mArrMaxIndex) {
		this.mArrMaxIndex = mArrMaxIndex;
	}

	public int mStartSampleCnt = 0;
	public Boolean checkStartPoint()
	   {
	      int tempFreq = 50;
	      int tempIndex = -1;
	      int currentFreq = 0;
	      for(int i = -1; i < 2; i++)
	      {
	         currentFreq = (int) this.detectFreqByIdx(START_BIT_IDX, i);
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
				int prevFreq = this.detectFreq(_freq - Constants.CHANNEL_INTERVAL);
				int nextFreq = this.detectFreq(_freq + Constants.CHANNEL_INTERVAL);

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
		if(dynRef > Constants.MAX_REF)
			dynRef = Constants.MAX_REF;
		if(dynRef < Constants.MIN_REF)
			dynRef = Constants.MIN_REF;

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
		if(mDynamicRefArray[freqIndex] > Constants.MAX_REF)
			mDynamicRefArray[freqIndex] = Constants.MAX_REF;
		if(mDynamicRefArray[freqIndex] < Constants.MIN_REF)
			mDynamicRefArray[freqIndex] = Constants.MIN_REF;

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

}
