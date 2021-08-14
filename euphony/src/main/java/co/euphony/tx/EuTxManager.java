package co.euphony.tx;

import android.content.Context;

import co.euphony.util.EuOption;

public class EuTxManager {
	private EuphonyTx txCore;
	private EuOption mTxOption = null;
	private String genCode = "";

	public EuTxManager(Context context) {
		txCore = new EuphonyTx(context);
	}

	public void setOption(EuOption option) {
        mTxOption = option;
        // mCodeMaker = new EuCodeMaker(mTxOption);
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

	public String getGenCode() {
		return genCode;
	}

	public short[] getOutStream() {

		/* TODO: legacy code will be removed.
		    That will be bring from EuphonyTx.
		return mOutStream;
		 */
		return null;
	}

	public void process() { process(1); }

	public void process(int count)
	{
		txCore.setCountToneOn(true, count);

		/*
		* TODO: legacy code will be removed.
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
		 */
	}
	
	public void stop()
	{
		txCore.setToneOn(false);
		/*
		* TODO: legacy code will be removed.
		if(mAudioTrack != null)
			mAudioTrack.pause();
		 */
	}
	
	public void setSoftVolume(float ratio)
	{
		/*
		* TODO: legacy soft volume adjustment.
		*  That will be created using EuphonyTx.
		for(int i = 0; i < mOutStream.length; i++)
			mOutStream[i] *= ratio;

		 */
	}
}
