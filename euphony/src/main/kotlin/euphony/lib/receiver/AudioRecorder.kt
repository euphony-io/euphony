package euphony.lib.receiver

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.nio.ByteBuffer

class AudioRecorder
/**
 * Constructor.
 *
 * @param sampleRate
 * The sample rate (e.g. 44100 Hz).
 * @param bufferSize
 * The size of the recording buffer in bytes.
 */
(sampleRate: Int) {

    private val audioRecord: AudioRecord
    private var running: Boolean = false
    private var swtWindowing: Boolean = false //TEST WINDOWING

    init {
        var bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)

        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize)
        swtWindowing = false
    }

    /**
     * Reads the mic input and writes to a buffer.
     *
     * @param buffer
     * The buffer that will store the samples.
     * @param bufferSize
     * The max number of bytes to be read.
     * @return The number of bytes that were read.
     */
    fun read(buffer: ByteBuffer, bufferSize: Int): Int {
        val samplesRead: Int
        samplesRead = audioRecord.read(buffer, bufferSize)
        return samplesRead
    }

    fun read(buffer: ByteBuffer, bufferSize: Int, windowNum: Short): Int {
        val samplesRead: Int
        samplesRead = audioRecord.read(buffer, bufferSize)
        swtWindowing = swtWindowing xor true
        if (swtWindowing) {
            val euWindow = EuWindows(windowNum, buffer, bufferSize)
            euWindow.Processor()
        }
        return samplesRead
    }

    /**
     * Starts recording audio. Must be called before the first call to 'read'.
     */
    fun start() {
        if (!running) {
            audioRecord.startRecording()
            running = true
        }
    }

    /**
     * Stops recording audio.
     */
    fun stop() {
        if (running) {
            audioRecord.stop()
            running = false
        }
    }

    /**
     * Releases the allocated memory.
     */
    fun destroy() {
        stop()
        //audioRecord.release();
    }

}