package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class EuTransmitManager {
	Context mContext;
	AudioTrack mAudioTrack;
	EuCodeMaker mCodeMaker = new EuCodeMaker();
	EuDataEncoder mDataEncoder;
	
	Boolean isStarted = false;
	short[] mOutStream;
	
	public EuTransmitManager(Context _context)
	{
		mContext = _context;
	}
	
	@SuppressWarnings("deprecation")
	public void euInitTransmit(String data)
	{
		mOutStream = mCodeMaker.euAssembleData(data);
		
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STREAM);
		mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
		
		setSystemVolumeMax();
	}
	
	public void process()
	{
		mAudioTrack.write(mOutStream, 0, mOutStream.length);
		mAudioTrack.play();
	}
	
	public void Stop()
	{
		mAudioTrack.pause();
	}
	
	public void setSoftVolume(float ratio)
	{
		for(int i = 0; i < mOutStream.length; i++)
			mOutStream[i] *= ratio;
	}

	private void setSystemVolumeMax()
	{
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		
		am.setStreamVolume(
			AudioManager.STREAM_MUSIC,
			am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
			0);
	}	 
}
