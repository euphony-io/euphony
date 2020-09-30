package euphony.lib.transmitter

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import euphony.lib.util.COMMON

class EuPlayer {

    private var mSource: ShortArray? = null
    private var mAudioTrack: AudioTrack? = null
    private val DATA_LENGTH = COMMON.FFT_SIZE * 4//2048;
    private val mZeroSource = ShortArray(DATA_LENGTH)

    constructor() {}

    constructor(src: ShortArray) {
        this.setSource(src)
    }

    fun setSource(src: ShortArray) {
        mSource = src
        mAudioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, src.size * 2, AudioTrack.MODE_STATIC)
    }

    fun play() {
        mAudioTrack!!.write(mSource!!, 0, mSource!!.size)
        mAudioTrack!!.setLoopPoints(0, mSource!!.size, -1)
        mAudioTrack!!.play()
    }

    fun stop() {
        if (mAudioTrack != null)
            if (mAudioTrack!!.playState == AudioTrack.PLAYSTATE_PLAYING)
                mAudioTrack!!.pause()
    }
}