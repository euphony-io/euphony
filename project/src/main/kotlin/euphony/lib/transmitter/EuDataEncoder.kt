package euphony.lib.transmitter

import android.util.Log
import euphony.lib.util.EuCodec
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

class EuDataEncoder : EuCodec {
    var originalSource: String? = null

    internal constructor() {}
    constructor(_source: String) {
        originalSource = _source
    }

    fun encodeHexCharSource(): String {
        return encodeStaticHexCharSource(originalSource)
    }

    fun encodeStaticHexCharSource(_source: String?): String {
        val strBuilder = StringBuilder()

        for (i in 0 until _source!!.length) {
            val data = _source[i].toInt()
            strBuilder.append(Integer.toHexString(data))
        }

        return strBuilder.toString()
    }

    companion object {

        fun base40StaticEncode(_source: String): ByteArray {
            try {
                val baos = ByteArrayOutputStream()
                val dos = DataOutputStream(baos)
                var i = 0
                while (i < _source.length) {
                    when (Math.min(3, _source.length - i)) {
                        1 -> {
                            val b = EuCodec.base40Index[_source[i] as Int]
                            dos.writeByte(b.toInt())
                        }
                        2 -> {
                            val ch = (EuCodec.base40Index[_source[i + 1] as Int] * 40 + EuCodec.base40Index[_source[i] as Int]).toChar()
                            dos.writeChar(ch.toInt())
                        }
                        3 -> {
                            val ch2 = ((EuCodec.base40Index[_source[i + 2] as Int] * 40 + EuCodec.base40Index[_source[i + 1] as Int]).toChar().toInt() * 40 + EuCodec.base40Index[_source[i] as Int]).toChar()
                            dos.writeChar(ch2.toInt())
                        }
                    }
                    i += 3
                }
                return baos.toByteArray()
            } catch (e: IOException) {
                throw AssertionError(e)
            }

        }
    }
}