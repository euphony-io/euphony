package euphony.lib.transmitter;

import euphony.lib.util.COMMON;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class EuPlayer {
	
	short[] mSource;
	AudioTrack mAudioTrack = null;
	public final int DATA_LENGTH = COMMON.FFT_SIZE * 4;//2048;
	private short[] mZeroSource = new short[DATA_LENGTH];
	
	public EuPlayer() { }
	
	public EuPlayer(short[] src)
	{
		this.setSource(src);
	}
	
	public void setSource(short[] src)
	{
		mSource = src;
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, src.length*2, AudioTrack.MODE_STATIC);
	}
	
	public void Play()
	{
		mAudioTrack.write(mSource, 0, mSource.length);
		mAudioTrack.setLoopPoints(0, mSource.length, -1);
		mAudioTrack.play();
	}
	
	public void Stop()
	{
		if(mAudioTrack != null)
			if(mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
				mAudioTrack.pause();
	}
}
