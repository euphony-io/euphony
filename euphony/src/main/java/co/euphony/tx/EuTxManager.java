package co.euphony.tx;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import co.euphony.common.Constants;
import co.euphony.common.EuNativeConnector;
import co.euphony.util.EuOption;

import static android.media.AudioTrack.SUCCESS;

public class EuTxManager {
	private EuNativeConnector txCore;
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

	public EuTxManager() {
		txCore = EuNativeConnector.getInstance();
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
		Constants.Result res = txCore.tx_start();

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
		txCore.tx_start();
	}

	private void playWithAndroidEngine(int count) {
		final float[] outStream = txCore.getGenWaveSource();
		final int minBufferSizeBytes = AudioTrack.getMinBufferSize(Constants.SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT);
		final int bufferSize = outStream.length * minBufferSizeBytes;
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT, bufferSize, AudioTrack.MODE_STATIC);

		/* A value of -1 means infinite looping, and 0 disables looping. */
		if(count <= 0)
			count = -1;

		final int result = mAudioTrack.setLoopPoints(0, outStream.length, count);
		if(result != SUCCESS) {
			Log.i("PROCESS", "failed to loop points : " + result);
		}

		if(mAudioTrack != null){
			try {
				mAudioTrack.write(outStream, 0, outStream.length, AudioTrack.WRITE_NON_BLOCKING);
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
			txCore.tx_stop();
		}
	}

	/*
	 * @deprecated Replaced by {@link #EuTxManager()}, deprecated for using context argument
	 */
	@Deprecated
	public EuTxManager(Context context) {
		txCore = EuNativeConnector.getInstance();
	}

	/*
	 * @deprecated Replaced by {@link #setCode(String)}, deprecated for naming & dynamic option.
	 */
	@Deprecated
	public void euInitTransmit(String data) {
		setCode(data);
	}

	/*
	 * @deprecated Replaced by {@link #play()}, deprecated for naming issue
	 */
	@Deprecated
	public void process() { play(1, PlayerEngine.ANDROID_DEFAULT_ENGINE); }

	/*
	 * @deprecated Replaced by {@link #play(int)}, deprecated for naming issue
	 */
	@Deprecated
	public void process(int count) { play(count, PlayerEngine.ANDROID_DEFAULT_ENGINE); }

}
