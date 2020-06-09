package euphony.lib.receiver;

import euphony.lib.util.EuOption;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.nio.FloatBuffer;

public class EuRxManager {

	private final String LOG = "EuRxManager";

	private Thread mListenThread = null;
	private DetectRunner mDetectRunner = null;

	public enum RxManagerStatus {
		RUNNING, STOP
	}

	private static final int RX_MODE = 1;
	private static final int PS_MODE = 2;
	private static final int DETECT_MODE = 3;

	private EuOption mOption;
	public EuRxManager() {
		mOption = new EuOption();
	}
	public EuRxManager(EuOption option) { mOption = option; }
	
	public boolean listen()
	{
		return listen(mOption);
	}

	public boolean listen(int freq) {
		return listen(mOption, freq);
	}

	public boolean listen(EuOption option) {
		if(getStatus() != RxManagerStatus.RUNNING) {
			switch (option.getCommunicationMode()) {
				case GENERAL:
				case LIVE:
					mListenThread = new Thread(new RxRunner(option), "RX");
					break;
				case FIND:
					mListenThread = new Thread(new PsRunner(option), "PS");
					break;
				case DETECT:
					Log.d(LOG, "Detect must have specific frequency value");
					return false;
			}

			mListenThread.start();
			return true;
		} else {
			return false;
		}

	}

	public boolean listen(EuOption option, int freq) {
		if(getStatus() != RxManagerStatus.RUNNING) {
			switch (option.getCommunicationMode()) {
				case GENERAL:
				case LIVE:
				case FIND:
					Log.d(LOG, "Please use other listen function.");
					return false;
				case DETECT:
					mDetectRunner = new DetectRunner(option, freq);
					mListenThread = new Thread(mDetectRunner, "DETECT");
					break;
			}

			mListenThread.start();
			return true;
		} else {
			return false;
		}
	}

	public void finish()
	{
		if(mListenThread != null) {
			mListenThread.interrupt();
		}
		
		mListenThread = null;
	}

	public RxManagerStatus getStatus() {
		if(mListenThread != null) {
			switch (mListenThread.getState()) {
				case RUNNABLE:
					return RxManagerStatus.RUNNING;
				case NEW:
				case WAITING:
				case TIMED_WAITING:
				case BLOCKED:
				case TERMINATED:
				default:
					return RxManagerStatus.STOP;
			}
		} else
			return RxManagerStatus.STOP;
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
				case RX_MODE:
					mAcousticSensor.notify(msg.obj + "");
					break;
				case PS_MODE:
					mPositionDetector.detectSignal((Integer)msg.obj);
					break;
				case DETECT_MODE:
					mFrequencyDetector.detect((float)msg.obj);
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

	public EuOption getOption() {
		return mOption;
	}

	public void setOption(EuOption mOption) {
		this.mOption = mOption;
	}
	
	private class RxRunner extends EuFreqObject implements Runnable{
		RxRunner(EuOption option) {
			super(option);
		}
		@Override
		public void run() 
		{
			while (!Thread.currentThread().isInterrupted()) {
				processFFT();
				if (this.getStarted())
					catchSingleData();
				else
					this.setStarted(checkStartPoint());

				if (this.getCompleted()) {
					Message msg = mHandler.obtainMessage();
					msg.what = RX_MODE;
					switch (mRxOption.getEncodingType()) {
						case ASCII:
							msg.obj = EuDataDecoder.decodeStaticHexCharSource(getReceivedData());
							break;
						case HEX:
							msg.obj = getReceivedData();
							break;
					}
					this.setCompleted(false);
					mHandler.sendMessage(msg);
					destroyFFT();
					return;
				}
			}

			destroyFFT();
		}
	}

	private FrequencyDetector mFrequencyDetector;

	public FrequencyDetector getFrequencyDetector() {
		return mFrequencyDetector;
	}

	public void setFrequencyDetector(FrequencyDetector mFrequencyDetector) {
		this.mFrequencyDetector = mFrequencyDetector;
	}

	public void setFrequencyForDetect(int freq) {
		if(mOption.getCommunicationMode() == EuOption.CommunicationMode.DETECT) {
			if(mDetectRunner != null)
				mDetectRunner.setFrequency(freq);
		}
	}


	private class DetectRunner extends EuFreqObject implements Runnable {

		int mFrequency = 0;
		private int mFreqIndex = 0;
		DetectRunner(EuOption option, int freq) {
			super(option);
			setFrequency(freq);
		}

		public void setFrequency(int frequency) {
			mFrequency = frequency;
			mFreqIndex = ((int)((frequency * mOption.getFFTSize()) / 2)) + 1;
		}

		@Override
		public void run() {
			float previousAmp = 0;

			while (!Thread.currentThread().isInterrupted()) {
				processFFT();
				float amp = getSpectrumValue(mFreqIndex);

				if (previousAmp != amp) {
					Message msg = mHandler.obtainMessage();
					msg.what = DETECT_MODE;
					msg.obj = amp;
					mHandler.sendMessage(msg);
					previousAmp = amp;
				}
			}

			destroyFFT();

		}
	}
	
	private class PsRunner extends EuFreqObject implements Runnable {

		PsRunner(EuOption option) {
			super(option);
		}

		@Override
		public void run() {
			boolean startswt = false;
			int startcnt = 0;
			int specificFreq = 0;
			Log.i("START", "START LISTEN");
			while(!Thread.currentThread().isInterrupted()){
				//To find the frequency point
				while(!startswt) {
					processFFT();
					int i;
					for(i = 21000; i >= 16500; i-= mRxOption.getDataInterval())
						if(100 < detectFreq(i)){
							startswt = true;
							break;
						}
					specificFreq = i;
					
					//there is no af area..
					if(startcnt++ > 1000){
						startswt = true;
						Log.i("START", "FAILED to find any position");
					}
				}
				
				int signal, max_signal = 0, avr_signal = 0;
				int noSignalCnt=0, processingCnt = 0, maxCnt=0;
				do{
					processFFT();
					signal = detectFreq(specificFreq);
					
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
							Message msg = mHandler.obtainMessage();
							msg.what = PS_MODE;
							msg.obj = avr_signal;
							mHandler.sendMessage(msg);
							processingCnt = 0;
							max_signal = 0;
							avr_signal = 0;
							maxCnt = 0;
						}
					}
				}while(noSignalCnt < 50 && startswt);

				destroyFFT();
				
				Message msg = mHandler.obtainMessage();
				msg.what = PS_MODE;
				msg.obj = -1;
				mHandler.sendMessage(msg);
				break;
				
			}
		}
	}
}
