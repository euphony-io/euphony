package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import euphony.lib.util.EuOption;

public class EuTxManager {
	private AudioTrack mAudioTrack = null;
	private EuOption mTxOption = null;
	private EuCodeMaker mCodeMaker = new EuCodeMaker();
	private EuDataEncoder mDataEncoder = new EuDataEncoder();

	public short[] getOutStream() {
		return mOutStream;
	}

	private short[] mOutStream;
	
	public EuTxManager() {
		mTxOption = new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.GENERAL);
	}

	public EuTxManager(EuOption option) {
		mTxOption = option;
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
		String code = data;

		// set encoding code.
		switch(mTxOption.getEncodingType()) {
			case ASCII:
				code = mDataEncoder.encodeStaticHexCharSource(data);
				break;
			case HEX:
				break;
		}

		// set communication mode.
		switch(mTxOption.getCommunicationMode()) {
			case GENERAL:
				mOutStream = mCodeMaker.assembleData(code);
				break;
			case LIVE:
				mOutStream = mCodeMaker.assembleLiveData(code);
				break;
		}
	}

	public void process() { process(1); }

	public void process(int count)
	{
		if(count > 0)
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STREAM);
		else {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream.length*2, AudioTrack.MODE_STATIC);
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
