package co.euphony.tx

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log
import co.euphony.common.Constants
import co.euphony.common.EuNativeConnector
import co.euphony.util.EuOption.ModeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EuTxManager {
    private val txCore: EuNativeConnector = EuNativeConnector.getInstance()
    private var audioTrack: AudioTrack? = null
    private var playerEngineType: PlayerEngine? = null
    var modeType: ModeType? = null
        set(value) {
            txCore.setMode(value)
            field = value
        }
    var code: String?
        get() = txCore.code
        set(data) {
            txCore.code = data
        }

    companion object {
        @JvmStatic
        fun getInstance() = Holder.INSTANCE
    }

    private object Holder {
        val INSTANCE = EuTxManager()
    }

    enum class EuPIDuration {
        LENGTH_SHORT, LENGTH_LONG, LENGTH_FOREVER
    }

    enum class PlayerEngine {
        ANDROID_DEFAULT_ENGINE, EUPHONY_NATIVE_ENGINE
    }

    fun callEuPI(freq: Double, duration: EuPIDuration): Constants.Result {
        modeType = ModeType.EUPI
        txCore.setAudioFrequency(freq)
        val res = txCore.tx_start()
        if (duration != EuPIDuration.LENGTH_FOREVER) {
            CoroutineScope(Dispatchers.IO).launch {
                delay(if (duration == EuPIDuration.LENGTH_SHORT) 200L else 500L)
                stop()
            }
        }
        return res
    }

    val outStream: FloatArray
        get() = txCore.genWaveSource
    val genCode: String
        get() = txCore.genCode

    @JvmOverloads
    fun play(count: Int = 1, engineType: PlayerEngine = PlayerEngine.ANDROID_DEFAULT_ENGINE) {
        playerEngineType = engineType
        modeType = ModeType.DEFAULT
        if (engineType == PlayerEngine.EUPHONY_NATIVE_ENGINE) {
            playWithNativeEngine(count)
        } else {
            playWithAndroidEngine(count)
        }
    }

    private fun playWithNativeEngine(count: Int) {
        txCore.setCountToneOn(true, count)
        txCore.tx_start()
    }

    private fun playWithAndroidEngine(count: Int) {
        var loopCount = count
        val outStream = txCore.genWaveSource
        val minBufferSizeBytes = AudioTrack.getMinBufferSize(
            Constants.SAMPLERATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
        )
        val bufferSize = outStream.size * minBufferSizeBytes
        audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .setSampleRate(Constants.SAMPLERATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                        .build()
                )
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
        } else {
            AudioTrack(
                AudioManager.STREAM_MUSIC,
                Constants.SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_FLOAT,
                bufferSize,
                AudioTrack.MODE_STATIC
            )
        }

        /* A value of -1 means infinite looping, and 0 disables looping. */
        if (loopCount <= 0) loopCount = -1
        val result = audioTrack?.setLoopPoints(0, outStream.size, loopCount)
        if (result != AudioTrack.SUCCESS) {
            Log.i("PROCESS", "failed to loop points : $result")
        }
        audioTrack?.let {
            try {
                it.write(outStream, 0, outStream.size, AudioTrack.WRITE_NON_BLOCKING)
                it.play()
            } catch (e: IllegalStateException) {
                Log.i("PROCESS", e.message ?: "")
            }
        }
    }

    fun stop() {
        if (modeType == ModeType.DEFAULT && playerEngineType == PlayerEngine.ANDROID_DEFAULT_ENGINE) {
            audioTrack?.pause()
        } else {
            txCore.tx_stop()
        }
    }

    /*
     * @deprecated Replaced by {@link #setModeType(String)}, deprecated for naming.
     */
    @Deprecated("Use the `modeType` property")
    fun setMode(type: ModeType?) {
        modeType = type
    }

    /*
	 * @deprecated Replaced by {@link #setCode(String)}, deprecated for naming & dynamic option.
	 */
    @Deprecated("Use the `code` property.")
    fun euInitTransmit(data: String?) {
        code = data
    }

    /*
     * @deprecated Replaced by {@link #play()}, deprecated for naming issue
     */
    @Deprecated("Use play() function instead", ReplaceWith("play()"))
    fun process() {
        play()
    }

    /*
     * @deprecated Replaced by {@link #play(int)}, deprecated for naming issue
     */
    @Deprecated("Use play(count: int) function simply", ReplaceWith("play(count)"))
    fun process(count: Int) {
        play(count)
    }
}