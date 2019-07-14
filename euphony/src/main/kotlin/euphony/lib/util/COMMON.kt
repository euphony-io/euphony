package euphony.lib.util

interface COMMON {
    companion object {
        // RX & TX COMMON VARIABLES
        val SAMPLERATE = 44100
        val FFT_SIZE = 512
        val DATA_LENGTH = FFT_SIZE * 4
        val FADE_RANGE = DATA_LENGTH / 16

        val MAX_FREQ = 22050.0
        val START_FREQ = 18000
        val CHANNEL = 16
        val CHANNEL_SPAN = SAMPLERATE / FFT_SIZE    // Frequency Interval
        val BUNDLE_INTERVAL = CHANNEL_SPAN * CHANNEL

        // RX
        val MAX_REF = 4000
        val MIN_REF = 50

        val DEFAULT_REF = 500    // BASE Reference value
    }
}
