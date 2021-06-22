package co.euphony.rx;


import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class KissFFT {	
	
	static{
	        System.loadLibrary("native-legacy");
		/*
		System.loadLibrary("kissff"); // kiss_fft.c
		System.loadLibrary("kissfftr"); // kiss_fftr.c
		System.loadLibrary("kissfft"); // KissFFT.cpp
		 */
	}
	
        /** the pointer to the kiss fft object **/
        private final long handle;
 
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


        private native void doSpectrum(long handle, ShortBuffer samples, int sample_idx, long mag_result, long dbfs_result);

        public void doSpectrum(ShortBuffer samples, int sample_idx, long mag_result_address, long dbfs_result_addr)
        {
                doSpectrum(handle, samples, sample_idx, mag_result_address, dbfs_result_addr);
        }

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
        private native void doSpectrums(long handle, ShortBuffer samples, FloatBuffer spectrum);

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
        public void doSpectrums(ShortBuffer samples, FloatBuffer spectrum) {
                doSpectrums(handle, samples, spectrum);
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
        public void doSpectrums(ByteBuffer samples, FloatBuffer spectrum) {
                doSpectrums(samples.asShortBuffer(), spectrum);
        }

        private native void spectrum_for_phase(long handle, ShortBuffer samples, FloatBuffer spectrum);
        
        public void spectrum_for_phase(ShortBuffer samples, FloatBuffer spectrum)
        {
        	spectrum_for_phase(handle, samples, spectrum);
        }
        
        public void spectrum_for_phase(ByteBuffer samples, FloatBuffer spectrum)
        {
        	spectrum_for_phase(samples.asShortBuffer(), spectrum);
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
                getImagePart(handle, imag);
        }

        private native void getRealPart(long handle, ShortBuffer real);

        private native void getImagePart(long handle, ShortBuffer imag);
}