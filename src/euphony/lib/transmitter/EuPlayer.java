package euphony.lib.transmitter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class EuPlayer {
	
	short[] mSource;
	AudioTrack mAudioTrack;
	
	public EuPlayer(short[] src)
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
		mAudioTrack.pause();
	}
}
