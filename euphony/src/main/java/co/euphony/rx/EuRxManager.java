package co.euphony.rx;

import co.euphony.common.Constants;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EuRxManager {
	private Thread mRxThread = null;
	private RxRunner mRxRunner = null;
	private boolean _active;
	
	private static final int RX_DECODE = 1;

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
			mRxRunner.destroyFFT();
		
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
				processFFT();
				if(this.getStarted())
					catchSingleData();
				else
					this.setStarted(checkStartPoint());
				
				if(this.getCompleted()){
					Message msg = mHandler.obtainMessage();
					msg.what = RX_DECODE;
					msg.obj = (mHex) ? getReceivedData() : EuDataDecoder.decodeStaticHexCharSource(getReceivedData());
					this.setCompleted(false);
					mHandler.sendMessage(msg);
					mRxRunner.destroyFFT();
					return;
				}
			}		
		}
	}
}
