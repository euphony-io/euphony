package euphony.lib.receiver

import android.util.Log
import euphony.lib.util.EuCodec

class EuDataDecoder(var originalSource: String?) : EuCodec() {

    fun decodeHexCharSource(): String? {
        return decodeStaticHexCharSource(originalSource!!)
    }

    companion object {

        fun decodeStaticHexCharSource(_source: String?): String? {
            var retValue = ""
            var i = 0

            if(_source == null)
                return null

            while (i < _source.length - 1) {
                val data = Integer.parseInt("" + _source[i] + _source[i + 1], 16)
                retValue += data.toChar()
                Log.d("DECODED", _source + " : " + _source[i] + " " + _source[i + 1] + " " + retValue)
                i += 2
            }

            return retValue
        }

        fun decodeStaticHexCharSource(_intArray: IntArray): String {
            var retValue = ""
            var i = 0
            while (i < _intArray.size - 1) {
                retValue += (_intArray[i] * 16 + _intArray[i + 1]).toChar()
                i += 2
            }

            return retValue
        }
    }
}