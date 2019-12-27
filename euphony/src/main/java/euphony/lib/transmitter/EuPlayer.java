package euphony.lib.transmitter;

import euphony.lib.util.COMMON;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class EuPlayer {
	
	private short[] mSource;
	private AudioTrack mAudioTrack = null;
	private short[] mZeroSource;
	
	public EuPlayer(int bufferSize)
	{
		mZeroSource = new short[bufferSize];
	}

	public EuPlayer(short[] src)
	{
		this.setSource(src);
	}
	
	public void setSource(short[] src)
	{
		mSource = src;
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, src.length*2, AudioTrack.MODE_STATIC);
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
