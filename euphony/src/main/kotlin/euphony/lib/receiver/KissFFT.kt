package euphony.lib.receiver

import android.util.Log
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class KissFFT(numSamples: Int) {

    /** the pointer to the kiss fft object  */
    private val handle: Long

    init {
        handle = create(numSamples)
        Log.i("var", " $handle")
    }

    /**
     * Creates a new kiss fft object
     *
     * @param timeSize
     * the number of samples
     * @return the handle to the kiss fft object
     */
    private external fun create(timeSize: Int): Long

    /**
     * Destroys a kiss fft object
     *
     * @param handle
     * the handle to the kiss fft object
     */
    private external fun destroy(handle: Long)

    /**
     * Calculates the frequency spectrum of the given samples. There must be as
     * many samples as specified in the constructor of this class. Spectrum must
     * hold timeSize / 2 + 1 elements
     *
     * @param handle
     * the handle to the kiss fft object
     * @param samples
     * the samples in 16-bit signed PCM encoding
     * @param spectrum
     * the spectrum
     */
    private external fun spectrum(handle: Long, samples: ShortBuffer,
                                  spectrum: FloatBuffer)

    /**
     * Calculates the frequency spectrum of the given samples. There must be as
     * many samples as specified in the constructor of this class. Spectrum must
     * hold timeSize / 2 + 1 elements
     *
     * @param samples
     * the samples
     * @param spectrum
     * the spectrum
     */
    fun spectrum(samples: ShortBuffer, spectrum: FloatBuffer) {
        spectrum(handle, samples, spectrum)
    }

    /**
     * Calculates the frequency spectrum of the given samples. There must be as
     * many samples as specified in the constructor of this class. Spectrum must
     * hold timeSize / 2 + 1 elements
     *
     * @param samples
     * the samples
     * @param spectrum
     * the spectrum
     */
    fun spectrum(samples: ByteBuffer, spectrum: FloatBuffer) {
        spectrum(samples.asShortBuffer(), spectrum)
    }

    private external fun spectrum_for_phase(handle: Long, samples: ShortBuffer, specturm: FloatBuffer)

    fun spectrum_for_phase(samples: ShortBuffer, spectrum: FloatBuffer) {
        spectrum_for_phase(handle, samples, spectrum)
    }

    fun spectrum_for_phase(samples: ByteBuffer, spectrum: FloatBuffer) {
        spectrum_for_phase(samples.asShortBuffer(), spectrum)
    }

    /**
     * Releases all resources of this object
     */
    fun dispose() {
        destroy(handle)
    }

    fun getRealPart(real: ShortBuffer) {
        getRealPart(handle, real)
    }

    fun getImagPart(imag: ShortBuffer) {
        getImagPart(handle, imag)
    }

    private external fun getRealPart(handle: Long, real: ShortBuffer)

    private external fun getImagPart(handle: Long, imag: ShortBuffer)

    companion object {

        init {
            System.loadLibrary("kissff") // kiss_fft.c
            System.loadLibrary("kissfftr") // kiss_fftr.c
            System.loadLibrary("kissfft") // KissFFT.cpp
        }
    }


}