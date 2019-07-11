package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class EuTxManager {
	private AudioTrack mAudioTrack = null;
	private EuCodeMaker mCodeMaker = new EuCodeMaker();
	private EuDataEncoder mDataEncoder = new EuDataEncoder();

	private boolean mHex = false;
	private short[] mOutStream;
	
	public EuTxManager() { }

	public EuTxManager(boolean hex) {
		mHex = hex;
	}

	public void euInitTransmit(String data)
	{
		mOutStream = mCodeMaker.euAssembleData((mHex) ? data : mDataEncoder.encodeStaticHexCharSource(data));
	}

	public void process() { process(1); }

	public void process(int count)
	{
		if(count > 0)
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STREAM);
		else {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length * 2, AudioTrack.MODE_STATIC);
			count = -1;
		}

		mAudioTrack.setLoopPoints(0, mOutStream.length, count);

		if(mAudioTrack != null){
			try{
				mAudioTrack.write(mOutStream, 0, mOutStream.length);
				mAudioTrack.play();
			}
			catch(IllegalStateException e)
			{
				Log.i("PROCESS", e.getMessage());
			}
		}
	}
	
	public void stop()
	{
		if(mAudioTrack != null)
			mAudioTrack.pause();
	}
	
	public void setSoftVolume(float ratio)
	{
		for(int i = 0; i < mOutStream.length; i++)
			mOutStream[i] *= ratio;
	}

	public void setSystemVolumeMax(Context _context)
	{
		AudioManager am = (AudioManager) _context.getSystemService(Context.AUDIO_SERVICE);

		if (am != null) {
            am.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0);
        }
	}	 
}
