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

    class FFTProcessor : public FFTModel {
    public:
        FFTProcessor(int fft_size, int sample_rate);
        ~FFTProcessor();
        virtual Spectrums makeSpectrum(const short* src);
        virtual Spectrums makeSpectrum(const float* src);
        static Spectrums makeSpectrum(float *src, int numSamples);
        int getResultSize() const;

    private:
        static float shortToFloat(const short val);
        int frequencyToIndex(const int freq) const;

        kiss_fftr_cfg config;
        /* unique_ptr's array version. it is available on c++14. */
        kiss_fft_cpx* spectrum;
        float* amplitudeSpectrum;
        float* phaseSpectrum;
        int fftSize;
        int halfOfFFTSize;
    };
}


#endif //EUPHONY_FFTPROCESSOR_H
