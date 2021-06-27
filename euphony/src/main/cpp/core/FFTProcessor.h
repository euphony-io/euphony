//
// Created by opener on 20. 11. 30.
//

#ifndef EUPHONY_FFTPROCESSOR_H
#define EUPHONY_FFTPROCESSOR_H

#include <memory>
#include "../arms/kiss_fftr.h"
#include "IFFTProcessor.h"

/*
 * Define FFT Structure
 * This space is similar to device driver's structure.
 * Euphony is now using KissFFT Library.
 * If you want to change FFT Library, Just fulfill IFFTProcessor interface.
 */

struct KissFFT
{
    kiss_fftr_cfg config;
    /* unique_ptr's array version. it is available on c++14. */
    kiss_fft_cpx* spectrum;
    float* result;
    int numSamples;
    int samplerate;
};

namespace Euphony {
    class FFTProcessor : IFFTProcessor {
    public:
        virtual void create(int fft_size, int samplerate);

        virtual void destroy();

        virtual float doSpectrum(int spectrum_idx);

        virtual float *doSpectrums(int from_idx, int to_idx, short *src);

        FFTProcessor(int fft_size, int samplerate);

        virtual ~FFTProcessor() = 0;

    private:
        inline float shortToFloat(short val);

        inline int frequencyToIndex(int freq);

        std::unique_ptr<KissFFT> mFFT;
    };
}


#endif //EUPHONY_FFTPROCESSOR_H
