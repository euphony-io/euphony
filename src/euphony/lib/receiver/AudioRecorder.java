package euphony.lib.receiver;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {

	private AudioRecord audioRecord;
	private boolean running;

	/**
	 * Constructor.
	 * 
	 * @param sampleRate
	 *            The sample rate (e.g. 44100 Hz).
	 * @param bufferSize
	 *            The size of the recording buffer in bytes.
	 */
	public AudioRecorder(int sampleRate, int bufferSize) {
		bufferSize = AudioRecord.getMinBufferSize(sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);

		Log.d("CommaTuner", "Buffer Size: " + bufferSize);
	}

	/**
	 * Reads the mic input and writes to a buffer.
	 * 
	 * @param buffer
	 *            The buffer that will store the samples.
	 * @param bufferSize
	 *            The max number of bytes to be read.
	 * @return The number of bytes that were read.
	 */
	public int read(ByteBuffer buffer, int bufferSize) {
		
		int samplesRead = audioRecord.read(buffer, bufferSize);
	//	euWindows euWindow = new euWindows(euWindows.BLACKMAN,buffer,bufferSize);
	//	euWindow.Processor();
		return samplesRead;
	}
	
	
	/*public int read(ByteBuffer buffer, int bufferSize) {
		int samplesRead = audioRecord.read(buffer, bufferSize);
		Log.i("buffer", "bytebuffer = "+buffer);

		byte[] rawdata = buffer.array();
		for(int i = 0; i < rawdata.length; i++)
			rawdata[i] = (byte) ((rawdata[i]) * ( 0.5 * (1-Math.cos( (2 * Math.PI * i) /    (512 - 1))))) ;	

		buffer = ByteBuffer.wrap(rawdata);

		return samplesRead;
	}*/


	/**
	 * Starts recording audio. Must be called before the first call to 'read'.
	 */
	public void start() {
		if (!running) {
			audioRecord.startRecording();
			running = true;
		}
	}

	/**
	 * Stops recording audio.
	 */
	public void stop() {
		if (running) {
			audioRecord.stop();
			running = false;
		}
	}

	/**
	 * Releases the allocated memory.
	 */
	public void destroy() {
		stop();
		audioRecord.release();
	}

}
