//
// Created by opener on 20. 11. 30.
//

#ifndef EUPHONY_FFTPROCESSOR_H
#define EUPHONY_FFTPROCESSOR_H

#include <memory>
#include "../arms/kiss_fftr.h"
#include "FFTModel.h"

/*
 * Define FFT Structure
 * This space is similar to device driver's structure.
 * Euphony is now using KissFFT Library.
 * If you want to change FFT Library, Just fulfill FFTModel interface.
 */

namespace Euphony {

    class FFTProcessor : FFTModel {
    public:
        virtual void destroy();
        virtual float* makeSpectrum(short* src);
        int getResultSize() const;
        FFTProcessor(int fft_size, int samplerate);
        ~FFTProcessor();

    private:
        virtual void initialize(int fft_size, int samplerate);
        inline float shortToFloat(short val);
        inline int frequencyToIndex(int freq);

        kiss_fftr_cfg config;
        /* unique_ptr's array version. it is available on c++14. */
        kiss_fft_cpx* spectrum;
        float* result;
        int fftSize;
        int sampleRate;
    };
}


#endif //EUPHONY_FFTPROCESSOR_H
