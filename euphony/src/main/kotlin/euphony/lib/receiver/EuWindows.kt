package euphony.lib.receiver

import java.nio.ByteBuffer

class EuWindows(mWindowNumber: Short, buffer: ByteBuffer, bufferSize: Int) {
    var windowNumber: Short = 0

    var buffer: ByteBuffer? = null
    var bufferSize = 0
    private val mRawData: ByteArray
    var kaiserAlph = 32.0
    var kaiserWindowSize = 45.0
    var hammingAlph = 54.0
    var blackmanAlph = 0.16

    init {
        this.buffer = buffer
        this.bufferSize = bufferSize
        mRawData = buffer.array()
        windowNumber = mWindowNumber
    }

    internal fun Processor() {
        if (buffer != null && bufferSize != 0)
        // check buffer buffersize
        {
            when (windowNumber as Int) {
                RECTANGULAR -> RectangularWindow()
                TRIANGLAR -> TrianglarWindow()
                HANNING -> HanningWindow()
                HAMMING -> HammingWindow()
                BLACKMAN -> BlackmanWindow()
                KAISER -> KaiserWindow()
            }
        }
    }

    internal fun RectangularWindow() {
        for (i in mRawData.indices)
            mRawData[i] = (mRawData[i] * 1.0).toByte()
        buffer = ByteBuffer.wrap(mRawData)

    }

    internal fun TrianglarWindow() {
        for (i in mRawData.indices)
            mRawData[i] = (mRawData[i] * ((mRawData.size / 2.0 - Math.abs(i - (mRawData.size - 1.0) / 2.0)) / (mRawData.size / 2.0))).toByte()
        buffer = ByteBuffer.wrap(mRawData)
    }

    internal fun HanningWindow() {
        for (i in mRawData.indices)
            mRawData[i] = (mRawData[i] * (0.5 * (1 - Math.cos(2.0 * Math.PI * i.toDouble() / (512 - 1))))).toByte()
        buffer = ByteBuffer.wrap(mRawData)
    }

    internal fun HammingWindow() {
        for (i in mRawData.indices)
            mRawData[i] = (mRawData[i] * (hammingAlph + (1.0 - hammingAlph) * Math.cos(2.0 * Math.PI * i.toDouble() / mRawData.size + Math.PI))).toByte()
        buffer = ByteBuffer.wrap(mRawData)
    }

    internal fun BlackmanWindow() {
        for (i in mRawData.indices)
            mRawData[i] = (mRawData[i] * ((1.0 - blackmanAlph) / 2.0 - 0.5 * Math.cos(2.0 * Math.PI * i.toDouble() / (mRawData.size - 1.0)) + blackmanAlph / 2.0 * Math.cos(4.0 * Math.PI * i.toDouble() / (mRawData.size - 1.0)))).toByte()
        buffer = ByteBuffer.wrap(mRawData)
    }

    internal fun KaiserWindow() {
        val n = kaiserWindowSize / 2.0
        for (i in mRawData.indices) {
            mRawData[i] = (mRawData[i] * (Util.I0(kaiserAlph * Math.sqrt(1.0 - Math.pow(2.0 * (i.toDouble() - n) / kaiserWindowSize, 2.0))) / Util.I0(kaiserAlph))).toByte()
        }
        buffer = ByteBuffer.wrap(mRawData)
    }

    internal object Util {
        fun I0(x: Double): Double {
            val eps = 10E-9
            var n = 1
            var S = 1.0
            var D = 1.0
            while (D > eps * S) {
                val T = x / (2.0 * n)
                n++
                D *= T * T
                S += D
            }
            return S
        }
    }

    companion object {

        //windows
        val RECTANGULAR = 0
        val TRIANGLAR = 1
        val HANNING = 2
        val HAMMING = 3
        val BLACKMAN = 4
        val KAISER = 5
    }
}