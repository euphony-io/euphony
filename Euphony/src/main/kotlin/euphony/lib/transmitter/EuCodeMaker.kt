package euphony.lib.transmitter

import android.util.Log
import euphony.lib.util.PacketErrorDetector

class EuCodeMaker : EuFreqGenerator {
    internal var mMainString: String? = null
    internal var mCodeSource: ShortArray? = null
    var START_BIT = freqBasePoint - freqSpan

    private var mChannelMode = CHANNEL.EXINGLE

    enum class CHANNEL {
        SINGLE, MULTI, EXINGLE
    }

    constructor() {}

    constructor(channelMode: CHANNEL) {
        mChannelMode = channelMode
    }


    fun euAssembleData(data: String): ShortArray {
        var assembledData = euApplyCrossFade(euMakeStaticFrequency(START_BIT, 0))
        val payload = IntArray(data.length + 1)

        when (mChannelMode) {
            EuCodeMaker.CHANNEL.SINGLE -> for (i in 0 until data.length) {
                when (data[i]) {
                    '0' -> assembledData = euAppendRawData(assembledData, zeroSource)
                    '1' -> assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint, 0)))
                }
            }

            EuCodeMaker.CHANNEL.MULTI -> for (i in 0 until data.length) {
                when (data[i]) {
                    '0' -> {
                    }
                    '1' -> assembledData = euMixingRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint + freqSpan * (i + 1), 0)))
                }
            }

            EuCodeMaker.CHANNEL.EXINGLE -> for (i in 0 until data.length) {
                val ch = data[i]
                when (ch) {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint + freqSpan * (ch - '0'), 0)))
                    'a', 'b', 'c', 'd', 'e', 'f' -> assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint + freqSpan * (ch - 'a' + 10), 0)))
                }
            }
        }

        for (i in 0 until data.length) {
            when (data[i]) {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> payload[i] = data[i] - '0'
                'a', 'b', 'c', 'd', 'e', 'f' -> payload[i] = data[i] - 'a' + 10
            }
        }
        val checksum = PacketErrorDetector.makeCheckSum(payload)
        val parity = PacketErrorDetector.makeParellelParity(payload)

        Log.i("UHEHE", "CHECKSUM : $checksum PARITY : $parity")
        assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint + freqSpan * checksum, 0)))
        assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(freqBasePoint + freqSpan * parity, 0)))

        assembledData = euAppendRawData(assembledData, euApplyCrossFade(euMakeStaticFrequency(START_BIT, 0)))

        return assembledData
    }

}