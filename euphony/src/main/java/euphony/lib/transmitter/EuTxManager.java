package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Toast;

public class EuTxManager {
	Context mContext;
	AudioTrack mAudioTrack = null;
	EuCodeMaker mCodeMaker = new EuCodeMaker();
	EuDataEncoder mDataEncoder;
	
	Boolean isStarted = false;
	short[] mOutStream;
	
	public EuTxManager(Context _context)
	{
		mContext = _context;
	}
	
	public void euInitTransmit(String data)
	{
		mOutStream = mCodeMaker.euAssembleData(data);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STREAM);
	}
	
	public void euInitTransmit(String data, int count)
	{
		mOutStream = mCodeMaker.euAssembleData(data, count);
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STREAM);
	}
	
	public void process()
	{
		if(mAudioTrack != null){
			try{
			mAudioTrack.write(mOutStream, 0, mOutStream.length);
			mAudioTrack.play();
			}
			catch(IllegalStateException e)
			{
				Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
				Log.i("PROCESS", e.getMessage());
			}
		}
	}
	
	public void Stop()
	{
		if(mAudioTrack != null)
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
