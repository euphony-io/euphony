package co.euphony.tx;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import co.euphony.common.Constants;
import co.euphony.util.EuOption;

import static android.media.AudioTrack.SUCCESS;
import static android.media.AudioTrack.WRITE_BLOCKING;

public class EuTxManager {
	private EuTxNativeConnector txCore;
	private AudioTrack mAudioTrack = null;
	private EuOption.ModeType modeType;
	private PlayerEngine playerEngineType;

	public enum EuPIDuration {
		LENGTH_SHORT,
		LENGTH_LONG,
		LENGTH_FOREVER
	}

	public enum PlayerEngine {
		ANDROID_DEFAULT_ENGINE,
		EUPHONY_NATIVE_ENGINE
	}

	public EuTxManager(Context context) {
		txCore = new EuTxNativeConnector(context);
	}

	public void setCode(String data)
	{
		txCore.setCode(data);
	}

	public String getCode() {
		return txCore.getCode();
	}

	public Constants.Result callEuPI(double freq, EuPIDuration duration) {
		setMode(EuOption.ModeType.EUPI);
		txCore.setToneOn(true);
		txCore.setAudioFrequency(freq);
		Constants.Result res = txCore.start();

		if (duration != EuPIDuration.LENGTH_FOREVER) {
			new Handler(Looper.getMainLooper()).postDelayed(this::stop,
					(duration == EuPIDuration.LENGTH_SHORT) ? 200 : 500);
		}

		return res;
	}

	public float[] getOutStream() {
		return txCore.getGenWaveSource();
	}

	public String getGenCode() {
		return txCore.getGenCode();
	}

	public void play() {
		play(1, PlayerEngine.ANDROID_DEFAULT_ENGINE);
	}

	public void play(final int count) {
		play(count, PlayerEngine.ANDROID_DEFAULT_ENGINE);
	}

	public void play(final int count, PlayerEngine engineType) {
		playerEngineType = engineType;
		setMode(EuOption.ModeType.DEFAULT);
		if(engineType == PlayerEngine.EUPHONY_NATIVE_ENGINE) {
			playWithNativeEngine(count);
		} else {
			playWithAndroidEngine(count);
		}
	}

	private void playWithNativeEngine(final int count) {
		txCore.setCountToneOn(true, count);
		txCore.start();
	}

	short[] outShortStream;
	private void playWithAndroidEngine(int count) {
		float[] outStream = txCore.getGenWaveSource();

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, outStream.length*2, AudioTrack.MODE_STATIC);
		if(count <= 0)
			count = -1;

		int result = mAudioTrack.setLoopPoints(0, outStream.length, count);
		if(result != SUCCESS) {
			Log.i("PROCESS", "failed to loop points : " + result);
		}

		outShortStream = new short[outStream.length];
		for(int i = 0; i < outStream.length; i++) {
			outShortStream[i] = (short) (32767 * outStream[i]);
		}

		if(mAudioTrack != null){
			try{
				mAudioTrack.write(outShortStream, 0, outShortStream.length);
				mAudioTrack.play();
			}
			catch(IllegalStateException e)
			{
				Log.i("PROCESS", e.getMessage());
			}
		}
	}

	public void setMode(EuOption.ModeType modeType) {
		this.modeType = modeType;
		txCore.setMode(modeType);
	}

	public void stop()
	{
		if(modeType == EuOption.ModeType.DEFAULT && playerEngineType == PlayerEngine.ANDROID_DEFAULT_ENGINE) {
			if(mAudioTrack != null)
				mAudioTrack.pause();
		}
		else {
			new Handler(Looper.getMainLooper()).postDelayed(() -> {
				txCore.stop();
			}, 300);
		}
	}

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming & dynamic option.
	 */
	@Deprecated
	public void euInitTransmit(String data) {
		setCode(data);
	}

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming issue
	 */
	@Deprecated
	public void process() { play(1, PlayerEngine.ANDROID_DEFAULT_ENGINE); }

	/*
	 * @deprecated Replaced by {@link #setCode()}, deprecated for naming issue
	 */
	@Deprecated
	public void process(int count) { play(count, PlayerEngine.ANDROID_DEFAULT_ENGINE); }

}
