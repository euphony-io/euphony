package euphony.lib.transmitter

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log

class EuTxManager {
    private var mAudioTrack: AudioTrack? = null
    private val mCodeMaker = EuCodeMaker()
    private val mDataEncoder = EuDataEncoder()

    private var mHex = false
    private var mOutStream: ShortArray? = null

    constructor() {}

    constructor(hex: Boolean) {
        mHex = hex
    }

    fun euInitTransmit(data: String) {
        mOutStream = mCodeMaker.euAssembleData(if (mHex) data else mDataEncoder.encodeStaticHexCharSource(data))
    }

    @JvmOverloads
    fun process(count: Int = 1) {
        var count = count
        if (count > 0)
            mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream!!.size * 2, AudioTrack.MODE_STREAM)
        else {
            mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, mOutStream!!.size * 2, AudioTrack.MODE_STATIC)
            count = -1
        }

        mAudioTrack!!.setLoopPoints(0, mOutStream!!.size, count)

        if (mAudioTrack != null) {
            try {
                mAudioTrack!!.write(mOutStream!!, 0, mOutStream!!.size)
                mAudioTrack!!.play()
            } catch (e: IllegalStateException) {
                Log.i("PROCESS", e.message)
            }

        }
    }

    fun stop() {
        if (mAudioTrack != null)
            mAudioTrack!!.pause()
    }

    fun setSystemVolumeMax(_context: Context) {
        val am = _context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0)
    }
}