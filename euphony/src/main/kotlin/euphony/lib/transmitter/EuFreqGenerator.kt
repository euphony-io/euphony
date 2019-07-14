package euphony.lib.transmitter

import euphony.lib.util.COMMON

open class EuFreqGenerator {

    // FIXED ACOUSTIC DATA
    val SAMPLERATE = COMMON.SAMPLERATE//44100;
    val DATA_LENGTH = COMMON.DATA_LENGTH//2048;
    val PI = Math.PI
    val PI2 = PI * 2

    // Member for Frequency point
    // DEFAULT DEFINITION
    var freqBasePoint = COMMON.START_FREQ
    var freqSpan = COMMON.CHANNEL_SPAN    //86
    val zeroSource = ShortArray(DATA_LENGTH)

    constructor() {}

    constructor(freqStartPoint: Int, freqSpan: Int) {
        this.freqSpan = freqSpan
        freqBasePoint = freqStartPoint
    }

    fun euMakeStaticFrequency(freq: Int, degree: Int): ShortArray {
        val double_source = DoubleArray(DATA_LENGTH)
        val source = ShortArray(DATA_LENGTH)
        var time: Double
        val phase: Double

        for (i in 0 until DATA_LENGTH) {
            time = i.toDouble() / SAMPLERATE.toDouble()
            double_source[i] = Math.sin(PI2 * freq.toDouble() * time)
            source[i] = (32767 * double_source[i]).toShort()
        }

        return source
    }

    fun euMakeFrequency(source: ShortArray, freq: Int) {
        var source = source
        source = euMakeStaticFrequency(freq, 0)
    }

    //updated
    fun euMakeFrequencyWithCrossFade(freq: Int): ShortArray {
        return euApplyCrossFade(euMakeStaticFrequency(freq, 0))
    }

    fun euApplyCrossFade(source: ShortArray): ShortArray {
        var mini_window: Double
        val fade_section = COMMON.FADE_RANGE
        for (i in 0 until fade_section) {
            mini_window = i.toDouble() / fade_section.toDouble()
            source[i] = (source[i].toDouble() * mini_window).toShort()
            source[DATA_LENGTH - 1 - i] = (source[DATA_LENGTH - 1 - i].toDouble() * mini_window).toShort()
        }

        return source
    }

    fun euAppendRawData(src: ShortArray?, objective: ShortArray): ShortArray {
        val SRC_LENGTH: Int
        val TOTAL_LENGTH: Int
        if (src == null)
            SRC_LENGTH = 0
        else
            SRC_LENGTH = src.size
        TOTAL_LENGTH = SRC_LENGTH + objective.size

        val dest = ShortArray(TOTAL_LENGTH)

        for (i in 0 until SRC_LENGTH)
            dest[i] = src!![i]
        for (i in SRC_LENGTH until TOTAL_LENGTH)
            dest[i] = objective[i - SRC_LENGTH]

        return dest
    }

    fun euLinkRawData(vararg sources: ShortArray): ShortArray {
        val dest = ShortArray(sources.size * DATA_LENGTH)

        for (i in sources.indices)
            for (j in 0 until sources[i].size)
                dest[j + i * DATA_LENGTH] = sources[i][j]

        return dest
    }

    fun euLinkRawData(isCrossfaded: Boolean, vararg sources: ShortArray): ShortArray {
        val dest = ShortArray(sources.size * DATA_LENGTH)
        for (i in sources.indices) {
            var src = sources[i]
            if (isCrossfaded)
                src = euApplyCrossFade(sources[i])

            for (j in 0 until src.size)
                dest[j + i * DATA_LENGTH] = src[j]
        }

        return dest
    }

    fun euMixingRawData(vararg sources: ShortArray): ShortArray {
        val dest = sources[0].clone()

        for (i in 1 until sources.size) {
            for (j in 0 until sources[i].size) {
                dest[j] = ((dest[j].toInt() + sources[i][j].toInt()) / 2).toShort()
            }
        }
        return dest
    }

    fun euMakeMaximumVolume(source: ShortArray): ShortArray {
        var max = 0
        //SCAN FOR VOLUME UP
        for (i1 in source)
            if (max < Math.abs(i1.toInt()))
                max = i1.toInt()
        if (32767 == max)
            return source


        for (i in source.indices)
            source[i] = ( source[i] * (32767.0 / max.toDouble())).toShort()

        return source
    }


}