package euphony.lib.receiver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.util.Log;
import euphony.lib.util.PacketErrorDetector;

public class EuFreqObject {

	public final int SAMPLERATE = 44100;
	public final int fftsize = 512;
	public final int MAXREFERENCE = 4000;
	public final int MINREFERENCE = 50;
	public final double MAXFREQUENCY = 22050.0;
	public final int DEFAULT_REF = 500;
	public final int START_FREQ = 18000;
	public final int RXCHANEL = 16;	
	private final int STARTCHANNEL = 1;	
	private int mFreqSpan = 86;
	public int START_BIT = START_FREQ - mFreqSpan;
	public Boolean mStartSwt = false;
	public Boolean isCompleted = false;

	private ByteBuffer samples = allocateByteBuffer(fftsize);
	private FloatBuffer spectrum = allocateFloatBuffer(fftsize/2+1);
	public static String receiveStr = "";
	private String mReceivedData;

	public String getReceivedData() {
		return mReceivedData;
	}


	public void setReceivedData(String _receivedData) {
		this.mReceivedData = _receivedData;
	}

	private int[] mFreqArray = new int[RXCHANEL + STARTCHANNEL];
	private int[] mTempRef = new int[RXCHANEL + STARTCHANNEL];
	private int[] mRefCntIndexArray = new int[RXCHANEL + STARTCHANNEL];
	private int[] mDynamicRefArray = new int[RXCHANEL + STARTCHANNEL];

	private ArrayList<Integer> mChannelArrayList = new ArrayList<Integer>();

	private AudioRecorder recorder = new AudioRecorder(SAMPLERATE, fftsize * 2);
	private KissFFT FFT = new KissFFT(fftsize);

	public EuFreqObject()
	{
		//INIT DYNAMIC REFERENCE ARRAY..
		for (int i = 0; i < RXCHANEL + STARTCHANNEL; i++)
			mDynamicRefArray[i] = DEFAULT_REF;
	}


	public void StartFFT()
	{
		recorder.start();
		recorder.read(samples, fftsize);
		FFT.spectrum(samples, spectrum);
	}

	public void StartFFT(int windowsNum)
	{
		recorder.start();
		recorder.read(samples, fftsize, windowsNum);
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

		//f1 = spectrum.get(freqIndex-1);
		f2 = spectrum.get(freqIndex);
		//f3 = spectrum.get(freqIndex+1);

		fmax = f2;
		//if( fmax < f2 )	fmax = f2;
		//if( fmax < f3 )	fmax = f3;

		return (int)(fmax*100000);
	}

	private int mMaxIndex = -1;		
	private int mSampleIndex = 0;
	private int mSampleTemp = 0;
	private int mChannelTemp = 0;
	public int cntCheck = 0;


	public void euCatchSingleData() 
	{
		int currentFreq = 0;
		for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++)
		{ 
			if(i != 16)
				currentFreq = (int) this.euDetectFreq(START_FREQ + mFreqSpan*i);
			else
				currentFreq = (int) this.euDetectFreq(START_BIT);
			mDynamicRefArray[i] = euGetDynamicReference(currentFreq, i);			
			
			if(currentFreq>=mSampleTemp && currentFreq >=mDynamicRefArray[i]){
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
			for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++)
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
					if(mChannelArrayList.size()!=0){
						cntCheck++;
						Log.v("mChanelArrayList.size()","mChanelArrayList.size():" +mChannelArrayList.size());
						
						mReceivedData = "";
						int []a= new int [mChannelArrayList.size()-1];
						for(int i = 0 ; i< mChannelArrayList.size() -1; i++){
							a[i]=mChannelArrayList.get(i);
							if(a[i] > 9)
								mReceivedData += "" + (char)('a' + (a[i] - 10));
							else
								mReceivedData += "" + a[i];
								
						}
						
						Log.i("checksum","sumChecksum:"+ PacketErrorDetector.makeCheckSum(a)+"checksum:"+
						mChannelArrayList.get(mChannelArrayList.size()-1));
						
						if(PacketErrorDetector.makeCheckSum(a) == mChannelArrayList.get(mChannelArrayList.size()-1)){
							mStartSwt = false;			
							isCompleted = true;
							receiveStr = EuDataDecoder.decodeStaticHexCharSource(a);
						}
						mChannelArrayList.clear();
					}
				}
				else{
					mChannelArrayList.add(mMaxIndex);
				}
				Log.v("DATA","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"  tempxcnt :" + mChannelTemp +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
				Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
						+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]+" 16:"+mFreqArray[16]);
			}
			else 
			{
				mChannelArrayList.add(-1);
				Log.v("DATAX","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
				Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
						+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]+" 16:"+mFreqArray[16]);

			}
			mMaxIndex = -1;
			mSampleIndex = 0 ;
			mSampleTemp = 0;
			mChannelTemp = 0;
			for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++){////
				mFreqArray[i] = 0;////
			}/////
		}

	}
	private int []mArrMaxIndex = new int [RXCHANEL + STARTCHANNEL];		
	private int []mArrSampleIndex = new int [RXCHANEL + STARTCHANNEL];
	private int []mArrSampleTemp = new int [RXCHANEL + STARTCHANNEL];
	private int []mArrChannelTemp = new int [RXCHANEL + STARTCHANNEL];
	public int []mArrcntCheck = new int [RXCHANEL + STARTCHANNEL];
	public void euCatchMultiData() 
	{
		for(int j = 0; j < RXCHANEL + STARTCHANNEL ; j++)
		{ 
			mArrMaxIndex[j] = -1;
			mArrSampleIndex[j] = 0;
			mArrSampleTemp[j] = 0;
			mArrChannelTemp[j] = 0;
			mArrcntCheck[j] = 0;
			mFreqArray[j] = 0;
		}
		int []arrCurrentFreq = new int[RXCHANEL + STARTCHANNEL];
		for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++)
		{ 
			arrCurrentFreq[i] = (int) this.euDetectFreq(START_FREQ + mFreqSpan*i);
			if(arrCurrentFreq[i] >=200){
				//mArrSampleTemp[i] = arrCurrentFreq[i];
				mArrMaxIndex[i] = i;
			}
		}
		/*for(int i = 0; i < RXCHANEL + STARTCHANNEL ; i++){
			if(mArrMaxIndex[i] != -1){
				mFreqArray[i]++;
			}
			if(mArrSampleIndex[i] < 7)
			{  
				mArrSampleIndex[i]++;
			}
			else
			{ 
				for(int j = 0; j < RXCHANEL + STARTCHANNEL ; j++)
				{
					if(mFreqArray[j]>mArrChannelTemp[j])
					{
						mArrChannelTemp[j] = mFreqArray[j];
						mArrMaxIndex[j] = j;
					}
					//mbFreqArray[i] = 0;
				}

				if(mArrChannelTemp[i] > 2 && mArrMaxIndex[i] != -1)
				{
					if(mArrMaxIndex[i] == 0){
						if(mChanelArrayList.size()!=0){
							cntCheck++;
							Log.v("mChanelArrayList.size()","mChanelArrayList.size():" +mChanelArrayList.size());

							int []a = new int [mChanelArrayList.size()-1];
							for(int j = 0 ; j< mChanelArrayList.size() -1; j++){
								a[j]=mChanelArrayList.get(j);
							}

							Log.i("checksum","sumChecksum:"+ErrorHandler.makeCheckSum(a)+"checksum:"+
									mChanelArrayList.get(mChanelArrayList.size()-1));

							if(ErrorHandler.makeCheckSum(a) == mChanelArrayList.get(mChanelArrayList.size()-1)){
								mStartSwt = false;			
								isChecked = true;
								makeData(a);
							}
							mChanelArrayList.clear();
						}
					}
					else{
						mChanelArrayList.add(mMaxIndex);
					}
					Log.v("DATA","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"  tempxcnt :" + mChannelTemp +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
					Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
							+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]);
				}
				else 
				{
					mChanelArrayList.add(-1);
					Log.v("DATAX","DATAIndex : "+ mMaxIndex +"  mCheckedByte :" + mSampleIndex +"   Data :"+ (int) this.euDetectFreq(START_FREQ + mFreqSpan*(mMaxIndex)));
					Log.v("DaTA","0:"+mFreqArray[0]+" 1:"+mFreqArray[1]+" 2:"+mFreqArray[2]+" 3:"+mFreqArray[3]+" 4:"+mFreqArray[4]+" 5:"+mFreqArray[5]+" 6:"+mFreqArray[6]+" 7:"+mFreqArray[7]+" 8:"+mFreqArray[8]
							+" 9:"+mFreqArray[9]+" 10:"+mFreqArray[10]+" 11:"+mFreqArray[11]+" 12:"+mFreqArray[12]+" 13:"+mFreqArray[13]+" 14:"+mFreqArray[14]+" 15:"+mFreqArray[15]);

				}
				for(int j = 0; j < RXCHANEL + STARTCHANNEL ; j++)
				{ 
					mArrMaxIndex[j] = -1;
					mArrSampleIndex[j] = 0;
					mArrSampleTemp[j] = 0;
					mArrChannelTemp[j] = 0;
					mArrcntCheck[j] = 0;
					mFreqArray[j] = 0;
				}
			}*/
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

	/*
	public void makeData(int a[]){         
		for(int i = 0; i < a.length-1; i+=2){
			char charData = (char)(a[i]*16 + a[i+1]);
			receiveStr += charData;
			Log.d("charData", a[i]+" "+a[i+1]+" "+receiveStr);
		}
	}
	*/

	public int mStartSampleCnt = 0;
	public Boolean euCheckStartPoint()
	   {
	      int tempFreq = 50;
	      int tempIndex = -1;
	      int currentFreq = 0;
	      for(int i = -1; i < 2; i++)
	      {
	         currentFreq = (int) this.euDetectFreq(START_BIT + mFreqSpan*i);
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
