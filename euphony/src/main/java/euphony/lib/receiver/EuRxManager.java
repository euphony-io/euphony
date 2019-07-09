package euphony.lib.receiver;

import euphony.lib.util.COMMON;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EuRxManager {
	private Thread mRxThread = null;
	private RxRunner mRxRunner = null;
	private PsRunner mPsRunner = null;
	private Thread mPsThread = null;
	private boolean _active;
	
	private static final int RX_DECODE = 1;
	private static final int PS_DECODE = 2;

	private boolean mHex = false;
	public EuRxManager() { }
	public EuRxManager(boolean hex) { mHex = hex; }
	
	public void listen()
	{
		listen(mHex);
	}

	public void listen(boolean hex) {
		_active = true;
		mRxRunner = new RxRunner(hex);
		mRxThread = new Thread(mRxRunner, "RX");
		mRxThread.start();
	}
	
	public void find() 
	{
		_active = true;
		mPsRunner = new PsRunner();
		mPsThread = new Thread(mPsRunner, "PS");
		mPsThread.start();
	}
	
	public void finishToFind()
	{
		if(mPsThread != null) {
			_active = false;
			while (true) {
				try {
					mPsThread.join();
					break;
				} catch (InterruptedException e) {
					Log.i("FINISH", e.getMessage());
				}
			}
		}

		if(mPsRunner != null)
			mPsRunner.DestroyFFT();

		mPsThread = null;
		mPsRunner = null;
	}
	
	public void finish()
	{
		if(mRxThread != null) {
			_active = false;
			while (true) {
				try {
					mRxThread.join();
					break;
				} catch (InterruptedException e) {
					Log.i("FINISH", e.getMessage());
				}
			}
		}
		if(mRxRunner != null)
			mRxRunner.DestroyFFT();
		
		mRxThread = null;
		mRxRunner = null;
	}

	private AcousticSensor mAcousticSensor;
	
	public AcousticSensor getAcousticSensor() {
		return mAcousticSensor;
	}

	public void setAcousticSensor(AcousticSensor iAcousticSensor) {
		this.mAcousticSensor = iAcousticSensor;
	}
	
	private Handler mHandler = new Handler(){		
		public void handleMessage(Message msg){			
			switch(msg.what){
			case RX_DECODE:
				mAcousticSensor.notify(msg.obj + "");
				break;
			default:
				break;
			}
		}
	};
	
	private PositionDetector mPositionDetector;
	
	public PositionDetector getPositionDetector() {
		return mPositionDetector;
	}
	
	public void setPositionDetector(PositionDetector detector) {
		this.mPositionDetector = detector;
	}
	
	private Handler mPsHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
				case PS_DECODE:
					mPositionDetector.detectSignal((Integer)msg.obj);
					break;
			}
		} 
	};
	
	private class RxRunner extends EuFreqObject implements Runnable{
		boolean mHex = false;
		RxRunner() { }
		RxRunner(boolean hex) {
			mHex = hex;
		}
		@Override
		public void run() 
		{
			while (_active) 
			{
				StartFFT();		
				if(mStartSwt)
					euCatchSingleData();
				else
					mStartSwt = euCheckStartPoint();
				
				if(isCompleted){		
					Message msg = mHandler.obtainMessage();
					msg.what = RX_DECODE;
					msg.obj = (mHex) ? getReceivedData() : EuDataDecoder.decodeStaticHexCharSource(getReceivedData());
					isCompleted = false;
					mHandler.sendMessage(msg);
					mRxRunner.DestroyFFT();
					return;
				}
			}		
		}
	}
	
	private class PsRunner extends EuFreqObject implements Runnable {

		@Override
		public void run() {
			boolean startswt = false;
			int startcnt = 0;
			int specificFreq = 0;
			Log.i("START", "START LISTEN");
			while(_active) {
				//To find the frequency point
				while(!startswt) {
					StartFFT();
					int i;
					for(i = 21000; i >= 16500; i-=COMMON.CHANNEL_SPAN)
						if(100 < euDetectFreq(i)){
							startswt = true;
							break;
						}
					specificFreq = i;
					
					//there is no af area..
					if(startcnt++ > 1000){
						_active = false;
						startswt = true;
						Log.i("START", "FAILED to find any position");
					}
				}
				
				int signal, max_signal = 0, avr_signal = 0;
				int noSignalCnt=0, processingCnt = 0, maxCnt=0;
				do{
					StartFFT();
					signal = euDetectFreq(specificFreq);
					
					if(signal < 20)
						noSignalCnt++;
					else{
						noSignalCnt = 0;
						
						if(max_signal < signal){
							maxCnt++;
							max_signal = signal;
							avr_signal += max_signal;
						}
						if(++processingCnt > 50){
							avr_signal /= maxCnt;
							Message msg = mPsHandler.obtainMessage();
							msg.what = PS_DECODE;
							msg.obj = avr_signal;
							mPsHandler.sendMessage(msg);
							processingCnt = 0;
							max_signal = 0;
							avr_signal = 0;
							maxCnt = 0;
						}
					}
				}while(noSignalCnt < 50 && _active);
				
				Message msg = mPsHandler.obtainMessage();
				msg.what = PS_DECODE;
				msg.obj = -1;
				mPsHandler.sendMessage(msg);
				break;
				
			}
		}
	}
}
