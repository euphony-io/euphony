package euphony.lib.receiver;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {

	private AudioRecord audioRecord;
	private boolean running;
	private boolean swtWindowing; //TEST WINDOWING

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
		swtWindowing = false;
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
		int samplesRead;
		samplesRead = audioRecord.read(buffer, bufferSize);
		return samplesRead;
	}
	public int read(ByteBuffer buffer, int bufferSize, int windowNum) {
		int samplesRead;
		samplesRead = audioRecord.read(buffer, bufferSize);
		if(swtWindowing^=true){			
			EuWindows euWindow = new EuWindows(windowNum,buffer,bufferSize);
			euWindow.Processor();
		}
		return samplesRead;
	}	

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
		//audioRecord.release();
	}

}
