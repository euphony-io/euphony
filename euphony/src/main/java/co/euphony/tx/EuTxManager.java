package co.euphony.tx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import co.euphony.util.EuOption;

public class EuTxManager {
	private EuTxNativeConnector txCore;

	public enum EuPIDuration {
		LENGTH_SHORT,
		LENGTH_LONG,
		LENGTH_FOREVER
	}

	public EuTxManager(Context context) {
		txCore = new EuTxNativeConnector(context);
	}

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming & dynamic option.
	 */
	@Deprecated
	public void euInitTransmit(String data) {
		setCode(data);
	}

	public void setCode(String data)
	{
		txCore.setCode(data);
	}

	public String getCode() {
		return txCore.getCode();
	}

	public void callEuPI(double freq, EuPIDuration duration) {
		txCore.setMode(EuOption.ModeType.EUPI);
		txCore.setToneOn(true);
		txCore.setAudioFrequency(freq);
		txCore.start();

		if (duration != EuPIDuration.LENGTH_FOREVER) {
			new Handler(Looper.getMainLooper()).postDelayed(this::stop,
					(duration == EuPIDuration.LENGTH_SHORT) ? 200 : 500);
		}
	}

	public String getGenCode() {
		return txCore.getGenCode();
	}

	public short[] getOutStream() {

		/* TODO: legacy code will be removed.
		    That will be bring from EuphonyTx.
		return mOutStream;
		 */
		return null;
	}

	public void play() {
		play(1);
	}

	public void play(int count) {
		txCore.setMode(EuOption.ModeType.DEFAULT);
		txCore.setCountToneOn(true, count);
		txCore.start();
	}

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming issue
	 */
	@Deprecated
	public void process() { play(1); }

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming issue
	 */
	@Deprecated
	public void process(int count) { play(count); }

	public void stop()
	{
		txCore.setToneOn(false);
		new Handler(Looper.getMainLooper()).postDelayed(() -> {
			txCore.stop();
		},300);
	}
}
