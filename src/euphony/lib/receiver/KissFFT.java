package euphony.lib.receiver;


import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.util.Log;

/**
 * A class for spectral analysis using native KissFFT
 * 
 * @author mzechner
 * 
 */
public class KissFFT {
	
	
	
	static{
		System.loadLibrary("kissff"); // kiss_fft.c
		System.loadLibrary("kissfftr"); // kiss_fftr.c
		System.loadLibrary("kissfft"); // KissFFT.cpp
		//System.load("")
	}
	
	
        /** the pointer to the kiss fft object **/
        private final long handle;

        /**
         * Creates a new fft instance that can analyse numSamples samples. timeSize
         * must be a power of two.
         * 
         * @param numSamples
         *            the number of samples to be analysed.
         */
        
       
        
        
        public KissFFT(int numSamples) {
                handle = create(numSamples);
                Log.i("var", " "+handle);
        }

        /**
         * Creates a new kiss fft object
         * 
         * @param timeSize
         *            the number of samples
         * @return the handle to the kiss fft object
         */
        private native long create(int timeSize);

        /**
         * Destroys a kiss fft object
         * 
         * @param handle
         *            the handle to the kiss fft object
         */
        private native void destroy(long handle);

        /**
         * Calculates the frequency spectrum of the given samples. There must be as
         * many samples as specified in the constructor of this class. Spectrum must
         * hold timeSize / 2 + 1 elements
         * 
         * @param handle
         *            the handle to the kiss fft object
         * @param samples
         *            the samples in 16-bit signed PCM encoding
         * @param spectrum
         *            the spectrum
         */
        private native void spectrum(long handle, ShortBuffer samples,
                        FloatBuffer spectrum);

        /**
         * Calculates the frequency spectrum of the given samples. There must be as
         * many samples as specified in the constructor of this class. Spectrum must
         * hold timeSize / 2 + 1 elements
         * 
         * @param samples
         *            the samples
         * @param spectrum
         *            the spectrum
         */
        public void spectrum(ShortBuffer samples, FloatBuffer spectrum) {
                spectrum(handle, samples, spectrum);
        }

        /**
         * Calculates the frequency spectrum of the given samples. There must be as
         * many samples as specified in the constructor of this class. Spectrum must
         * hold timeSize / 2 + 1 elements
         * 
         * @param samples
         *            the samples
         * @param spectrum
         *            the spectrum
         */
        public void spectrum(ByteBuffer samples, FloatBuffer spectrum) {
                spectrum(samples.asShortBuffer(), spectrum);
        }

        /**
         * Releases all resources of this object
         */
        public void dispose() {
                destroy(handle);
        }

        public void getRealPart(ShortBuffer real) {
                getRealPart(handle, real);
        }

        public void getImagPart(ShortBuffer imag) {
                getImagPart(handle, imag);
        }

        private native void getRealPart(long handle, ShortBuffer real);

        private native void getImagPart(long handle, ShortBuffer imag);
        
        
        
        
        
        
        

}