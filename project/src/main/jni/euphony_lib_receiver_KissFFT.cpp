#include <malloc.h>
#include "../cpp/kiss_fftr.h"
#include "euphony_lib_receiver_KissFFT.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

#define MAX_SHORT 32767.0f

static inline float scale( kiss_fft_scalar val )
{
        if( val < 0 )
                return val * ( 1 / 32768.0f );
        else
                return val * ( 1 / 32767.0f );
}

struct KissFFT
{
        kiss_fftr_cfg config;
        kiss_fft_cpx* spectrum;
        int numSamples;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_euphony_lib_receiver_KissFFT_create (JNIEnv *, jobject, jint numSamples)
{
    KissFFT* fft = new KissFFT();

   fft->config = kiss_fftr_alloc(numSamples,0,NULL,NULL);
   fft->spectrum = (kiss_fft_cpx*)malloc(sizeof(kiss_fft_cpx) * (int)numSamples);

   //__android_log_print(ANDROID_LOG_INFO,"----","r: %f i : %f",fft->spectrum->r, fft->spectrum->i);
   fft->numSamples = numSamples;
    return (jlong)fft;
    return 0;
}

/*
 * Class:     euphony_lib_receiver_KissFFT
 * Method:    destroy
 * Signature: (J)V
 */
extern "C"
JNIEXPORT void JNICALL
Java_euphony_lib_receiver_KissFFT_destroy(JNIEnv *, jobject, jlong handle)
{
        KissFFT* fft = (KissFFT*)handle;
        free(fft->config);
        free(fft->spectrum);
        free(fft);
}

/*
 * Class:     euphony_lib_receiver_KissFFT
 * Method:    spectrum_for_phase
 * Signature: (JLjava/nio/ShortBuffer;Ljava/nio/FloatBuffer;)V
 */
extern "C"
JNIEXPORT void JNICALL
Java_euphony_lib_receiver_KissFFT_spectrum_1for_1phase
  (JNIEnv *env, jobject, jlong handle, jobject source, jobject target)
{
	KissFFT* fft = (KissFFT*)handle;
	kiss_fft_scalar* samples = (kiss_fft_scalar*)env->GetDirectBufferAddress( source );
	float* spectrum = (float*)env->GetDirectBufferAddress( target );

	kiss_fftr( fft->config, samples, fft->spectrum );

	int len = fft->numSamples / 2 + 1;
	int start = len * (16500.0 / 22050.0);

	for( int i = start; i < len; i++ )
	{
        float re = scale(fft->spectrum[i].r) * fft->numSamples;
        float im = scale(fft->spectrum[i].i) * fft->numSamples;

        spectrum[i] = atan2(im,re) * 180 / 3.141592;
	}
}

/*
 * Class:     euphony_lib_receiver_KissFFT
 * Method:    getRealPart
 * Signature: (JLjava/nio/ShortBuffer;)V
 */
extern "C"
JNIEXPORT void JNICALL
Java_euphony_lib_receiver_KissFFT_getRealPart(JNIEnv *env, jobject, jlong handle, jobject real)
{
        KissFFT* fft = (KissFFT*)handle;
        short* target = (short*)env->GetDirectBufferAddress(real);
        for( int i = 0; i < fft->numSamples / 2; i++ )
                target[i] = fft->spectrum[i].r;
}

/*
 * Class:     euphony_lib_receiver_KissFFT
 * Method:    getImagPart
 * Signature: (JLjava/nio/ShortBuffer;)V
 */
extern "C"
JNIEXPORT void JNICALL Java_euphony_lib_receiver_KissFFT_getImagPart(JNIEnv *env, jobject, jlong handle, jobject real)
{
        KissFFT* fft = (KissFFT*)handle;
        short* target = (short*)env->GetDirectBufferAddress(real);
        for( int i = 0; i < fft->numSamples / 2; i++ )
                target[i] = fft->spectrum[i].i;
}

extern "C"
JNIEXPORT void JNICALL
Java_euphony_lib_receiver_KissFFT_doSpectrum(JNIEnv *env, jobject thiz, jlong handle,
                                              jobject source, jint sample_idx, jlong magnitude_address, jlong dbfs_address) {
        KissFFT* fft = (KissFFT*)handle;
        kiss_fft_scalar* samples = (kiss_fft_scalar*)env->GetDirectBufferAddress( source );
        float* mag = (float*) magnitude_address;
        float* dbfs = (float*) dbfs_address;

        kiss_fftr( fft->config, samples, fft->spectrum );

        float re = scale(fft->spectrum[sample_idx].r) * fft->numSamples;
        float im = scale(fft->spectrum[sample_idx].i) * fft->numSamples;

        *mag = sqrtf( re * re + im * im ) / (fft->numSamples / 2);
        *dbfs = 10 * log10(4 * (re * re + im * im) / (fft->numSamples * fft->numSamples));
}

/*
 * Class:     euphony_lib_receiver_KissFFT
 * Method:    getSpectrums
 * Signature: (JLjava/nio/ShortBuffer;Ljava/nio/FloatBuffer;)V
 */
extern "C"
JNIEXPORT void JNICALL
Java_euphony_lib_receiver_KissFFT_doSpectrums(JNIEnv *env, jobject thiz, jlong handle,
                                               jobject source, jobject target) {
        KissFFT* fft = (KissFFT*)handle;
        kiss_fft_scalar* samples = (kiss_fft_scalar*)env->GetDirectBufferAddress( source );
        float* spectrum = (float*)env->GetDirectBufferAddress( target );


        kiss_fftr( fft->config, samples, fft->spectrum );    //<--- fatal signal 11 (SIGSEV) at 0x00000400

        int len = fft->numSamples / 2 + 1;  // <=---  <--- fatal signal 11 (SIGSEV) at 0x00000408
        int len_halfOfNumSamples = fft->numSamples / 2;
        // int len = 6; // <-- for debugging
        int start = len * (17500.0 / 22050.0);
        for( int i = start; i < len; i++ )
        {
                float re = scale(fft->spectrum[i].r) * fft->numSamples;
                float im = scale(fft->spectrum[i].i) * fft->numSamples;

                // mag(k) = 2 * SQUARE_ROOT(re * re + im * im) / number of samples
                spectrum[i] = sqrtf(re*re + im*im) / len_halfOfNumSamples;

                /*
                if( i > 0 ) spectrum[i] = sqrtf(re*re + im*im) / (fft->numSamples / 2);
                else spectrum[i] = sqrtf(re*re + im*im) / (fft->numSamples);
                */
        }
}