package co.euphony.rx;


import static co.euphony.rx.EuPI.EuPIStatus.KEY_DOWN;
import static co.euphony.rx.EuPI.EuPIStatus.KEY_PRESSED;
import static co.euphony.rx.EuPI.EuPIStatus.KEY_UP;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import co.euphony.common.EuNativeConnector;
import co.euphony.util.EuOption;
import co.euphony.util.EuTimeOutListener;
import co.euphony.util.EuTimer;

public class EuRxManager {

	private final String LOG = "EuRxManager";

	private EuNativeConnector nativeCore;
	private RxEngineType rxEngineType = RxEngineType.EUPHONY_JAVA_ENGINE;

	private Thread mListenThread = null;
	private final ArrayList<EuPI> mEuPIList = new ArrayList<>();

	public enum RxManagerStatus {
		RUNNING, STOP
	}

	public enum RxEngineType {
		EUPHONY_JAVA_ENGINE,
		EUPHONY_NATIVE_ENGINE
	}

	private static final int RX_MODE = 1;
	private static final int EUPI_CALL_MODE = 2;

	private EuOption mOption;

	private static class InnerInstanceClazz{
		private static final EuRxManager instance = new EuRxManager();
	}

	public static EuRxManager getInstance(){
		return InnerInstanceClazz.instance;
	}

	private EuRxManager(){
		this.mOption = EuOption.builder()
				.modeWith(EuOption.ModeType.DEFAULT)
				.encodingWith(EuOption.CodingType.BASE16)
				.modulationWith(EuOption.ModulationType.FSK)
				.build();
		Log.d(LOG, "EuRxManager creation was successful");
	}

	private boolean listenOnJava(long timeout, @Nullable EuTimeOutListener listener) {
		if (getStatus() != RxManagerStatus.RUNNING) {
			switch (mOption.getMode()) {
				case DEFAULT:
					mListenThread = new Thread(new RxRunner(), "RX");
					break;
				case EUPI:
					if (mEuPIList.size() > 0) {
						mListenThread = new Thread(new EuPICallRunner(mEuPIList), "EUPI");
						Log.d(LOG, "Euphony : EuPICallRunner's EuPI Count : " + mEuPIList.size());
					} else {
						Log.e(LOG, "Euphony : There are no EuPis");
						return false;
					}
					break;
				default:
					Log.d(LOG, "Detect must have specific frequency value");
					return false;
			}
			mListenThread.start();
			if (timeout > 0) {
				EuTimer euTimer = new EuTimer(mListenThread, () -> {
					if (listener != null) {
						listener.onTimeOut();
					}
					mListenThread = null;
				});
				euTimer.start(timeout);
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean listenOnNative() {
		/* TODO: 1) To implement the getRxStatus() */
		/* TODO: 2) To implement the native listener */
		nativeCore.rx_start();

		return false;
	}

	public boolean listen() {
		return listen(0, null);
	}

	public boolean listen(@IntRange(from = 0) long timeout) {
		return listen(timeout, null);
	}

	public boolean listen(@IntRange(from = 0) long timeout, EuTimeOutListener listener) {
		if (rxEngineType == RxEngineType.EUPHONY_JAVA_ENGINE)
			return listenOnJava(timeout, listener);
		else
			return listenOnNative();
	}

	public void finish() {
		if (rxEngineType == RxEngineType.EUPHONY_JAVA_ENGINE) {
			if (mListenThread != null) {
				mListenThread.interrupt();
			}
			mListenThread = null;
		} else {
			nativeCore.rx_stop();
		}
	}

	public void setOnWaveKeyPressed(int freq, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(freq, KEY_PRESSED, iEuPICallDetector));
	}

	public void setOnWaveKeyDown(int key, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(key, KEY_DOWN, iEuPICallDetector));
	}

	public void setOnWaveKeyUp(int key, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(key, KEY_UP, iEuPICallDetector));
	}

	public void setOnWaveKeyPressed(int freq, double threshold, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(freq, threshold, KEY_PRESSED, iEuPICallDetector));
	}

	public void setOnWaveKeyDown(int key, double threshold, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(key, threshold, KEY_DOWN, iEuPICallDetector));
	}

	public void setOnWaveKeyUp(int key, double threshold, EuPICallDetector iEuPICallDetector) {
		mEuPIList.add(new EuPI(key, threshold, KEY_UP, iEuPICallDetector));
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

	private final Handler mHandler = new Handler(Looper.getMainLooper()){
		public void handleMessage(Message msg){
			switch(msg.what){
				case RX_MODE:
					mAcousticSensor.notify(msg.obj + "");
					break;
				case EUPI_CALL_MODE:
					EuPI eupi = (EuPI)msg.obj;
					eupi.getCallback().call();
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

	public RxEngineType getRxEngineType() {
		return this.rxEngineType;
	}

	public void setRxEngineType(RxEngineType type) {
		this.rxEngineType = type;
	}

	private class RxRunner extends EuFreqObject implements Runnable{
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
					if(mOption.getCodingType() == EuOption.CodingType.BASE16) {
						msg.obj = EuDataDecoder.decodeStaticHexCharSource(getReceivedData());
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

	private class EuPICallRunner extends EuFreqObject implements Runnable {

		private ArrayList<EuPI> EuPICallList = null;

		EuPICallRunner(ArrayList<EuPI> eupiList) {
			for(EuPI eupi : eupiList) {
				eupi.setStatus(EuPI.EuPIStatus.KEY_UP);
			}
			EuPICallList = eupiList;
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				processFFT();

				for(EuPI eupi : EuPICallList) {
					int freqIndex = eupi.getFreqIndex();
					final float ampLeft = getSpectrumValue(freqIndex - 2);
					final float ampCenter = getSpectrumValue(freqIndex);
					final float ampRight = getSpectrumValue(freqIndex + 2);

					EuPI.EuPIStatus status = eupi.feedAmplitude(ampLeft, ampCenter, ampRight);
					if(eupi.getTrigger() == status) {
						Message msg = mHandler.obtainMessage();
						msg.what = EUPI_CALL_MODE;
						msg.obj = eupi;
						mHandler.sendMessage(msg);
					}
				}
			}
			Log.d(LOG, "Finished EuPICallRunner");
			destroyFFT();
		}
	}
}
