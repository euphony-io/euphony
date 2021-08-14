package co.euphony.rx;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import co.euphony.util.EuOption;

import static co.euphony.rx.EpnyAPI.EpnyAPITrigger.KEY_DOWN;
import static co.euphony.rx.EpnyAPI.EpnyAPITrigger.KEY_PRESSED;
import static co.euphony.rx.EpnyAPI.EpnyAPITrigger.KEY_UP;

public class EuRxManager {

	private final String LOG = "EuRxManager";

	private Thread mListenThread = null;
	private DetectRunner mDetectRunner = null;
	private APICallRunner mAPICallRunner = null;

	public enum RxManagerStatus {
		RUNNING, STOP
	}

	private static final int RX_MODE = 1;
	private static final int DETECT_MODE = 2;
	private static final int API_CALL_MODE = 3;

	private EuOption mOption;

	public EuRxManager() {
		mOption = new EuOption();
	}

	public EuRxManager(EuOption option) {
		mOption = option;
	}

	public EuRxManager(EuOption.CommunicationMode mode) {
		mOption = new EuOption();
		mOption.setCommunicationMode(mode);
	}
	
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
					mListenThread = new Thread(new RxRunner(option), "RX");
					break;
				case API: {
					if(mAPICallRunner != null) {
						mAPICallRunner.initialize(mAPICallRunner.getRxOption());
						mListenThread = new Thread(mAPICallRunner, "API");
						Log.d(LOG, "Euphony : APICallRunner's API Count : " + mAPICallRunner.getAPICount());
						break;
					}
					else {
						Log.d(LOG, "Euphony : APICallRunner is null");
						return false;
					}
				}
				default:
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
			if(option.getCommunicationMode() == EuOption.CommunicationMode.DETECT) {
				mDetectRunner = new DetectRunner(option, freq);
				mListenThread = new Thread(mDetectRunner, "DETECT");
				mListenThread.start();
				return true;
			} else {
				Log.d(LOG, "Please use other listen function.");
				return false;
			}
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

	public void setOnWaveKeyPressed(int freq, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(freq, KEY_PRESSED, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
	}

	public void setOnWaveKeyDown(int key, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(key, KEY_DOWN, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
	}

	public void setOnWaveKeyUp(int key, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(key, KEY_UP, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
	}

	public void setOnWaveKeyPressed(int freq, double threshold, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(freq, KEY_PRESSED, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, threshold, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
	}

	public void setOnWaveKeyDown(int key, double threshold, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(key, KEY_DOWN, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, threshold, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
	}

	public void setOnWaveKeyUp(int key, double threshold, APICallDetector iAPICallDetector) {
		EpnyAPI api = new EpnyAPI(key, KEY_UP, iAPICallDetector);

		if(mAPICallRunner == null) {
			mAPICallRunner = new APICallRunner(mOption, threshold, api);
		} else {
			mAPICallRunner.addAPI(api);
		}
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
				case DETECT_MODE:
					mFrequencyDetector.detect((float)msg.obj);
					break;
				case API_CALL_MODE:
					EpnyAPI api = (EpnyAPI)msg.obj;
					api.getCallback().call();
					break;

			default:
				break;
			}
		}
	};

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
					msg.obj = null;
					if(mRxOption.getEncodingType() == EuOption.EncodingType.HEX) {
							msg.obj = getReceivedData();
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

	private class APICallRunner extends EuFreqObject implements Runnable {

		private double mThreshold = 0.0009;
		private ArrayList<EpnyAPI> APICallList = new ArrayList<>();

		APICallRunner(EuOption option, EpnyAPI api) {
			super(option);
			addAPI(api);
			Log.d(LOG, "Added " + api.getKey() + "(" + api.getFreqIndex() + ")");
		}

		APICallRunner(EuOption option, double threshold, EpnyAPI api) {
			super(option);
			mThreshold = threshold;
			addAPI(api);
			Log.d(LOG, "Added " + api.getKey() + "(" + api.getFreqIndex() + ")");
		}

		private int calculateFreqIndex(int freq) {
			double freqRatio = ((float)freq) / 22050.0;
			return (int) Math.round((freqRatio * (float)mOption.getFFTSize() / 2));
			//( (int) (fFreqRatio * mRxOption.getFFTSize() / 2) ) + 1;
		}

		private boolean compareThreshold(float amp) {
			return amp > mThreshold;
		}

		public void addAPI(EpnyAPI api) {
			api.setFreqIndex(calculateFreqIndex(api.getKey()));
			APICallList.add(api);
		}

		public int getAPICount() {
			if(APICallList != null)
				return APICallList.size();
			else
				return 0;
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				processFFT();

				float[] amp = {0, 0, 0};
				for(EpnyAPI api : APICallList) {
					boolean isActable = false;
					switch(api.getTrigger()) {
						case KEY_DOWN: {
							if(api.getStatus() == EpnyAPI.EpnyAPIStatus.KEY_UP){
								isActable = true;
							}
						}
						break;
						case KEY_UP: {
							if(api.getStatus() != EpnyAPI.EpnyAPIStatus.KEY_UP) {
								isActable = true;
							}
						}
						break;
						case KEY_PRESSED: {
							isActable = true;
						}
						break;
					}

					int freqIndex = api.getFreqIndex();
					amp[0] = getSpectrumValue(freqIndex - 2);
					amp[1] = getSpectrumValue(freqIndex);
					amp[2] = getSpectrumValue(freqIndex + 2);

					if(isActable) {
						if(compareThreshold((amp[1] - (amp[0] + amp[2])/2))) {
							if(api.getTrigger() == KEY_DOWN || api.getTrigger() == KEY_PRESSED) {
								Message msg = mHandler.obtainMessage();
								msg.what = API_CALL_MODE;
								msg.obj = api;
								mHandler.sendMessage(msg);
							}

							api.setStatus(EpnyAPI.EpnyAPIStatus.KEY_DOWN);
						} else {
							if(api.getTrigger() == KEY_UP) {
								Message msg = mHandler.obtainMessage();
								msg.what = API_CALL_MODE;
								msg.obj = api;
								mHandler.sendMessage(msg);
							}

							api.setStatus(EpnyAPI.EpnyAPIStatus.KEY_UP);
						}
					} else {
						if(compareThreshold((amp[1] - (amp[0] + amp[2])/2))) {
							api.setStatus(EpnyAPI.EpnyAPIStatus.KEY_DOWN);
						} else {
							api.setStatus(EpnyAPI.EpnyAPIStatus.KEY_UP);
						}
					}

					Log.d(LOG, api.getKey() + "(" + api.getFreqIndex() + ")" + "'s Amplitude : " + amp[2]);
					/*
					Log.d(LOG, api.getId() + "(" + (api.getFreqIndex() - 1) + ")" + "'s Amplitude : " + amp[0]);
					Log.d(LOG, api.getId() + "(" + (api.getFreqIndex() - 1) + ")" + "'s Amplitude : " + amp[1]);

					Log.d(LOG, api.getId() + "(" + (api.getFreqIndex() + 1) + ")" + "'s Amplitude : " + amp[3]);
					Log.d(LOG, api.getId() + "(" + (api.getFreqIndex() - 1) + ")" + "'s Amplitude : " + amp[4]);
					Log.d(LOG, api.getId() + "(" + (api.getFreqIndex() - 1) + ")" + "'s Amplitude Diff : " + (amp[2] - (amp[0] + amp[4])/2));
					 */
				}
			}

			destroyFFT();
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
			mFreqIndex = ((int)((frequency / 22050.0) * mOption.getFFTSize()) / 2) + 1;
			Log.d(LOG, "Frequency = " + mFrequency + ", mFreqIndex = " + mFreqIndex);
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
}
