#ifndef EUPHONY_BLUEFFT_H
#define EUPHONY_BLUEFFT_H

#include "FFTModel.h"
#include <complex>
#include <vector>

namespace Euphony {

    constexpr double PI = 3.14159265358979323846264338327;
    typedef std::complex<float> fcpx;
    typedef std::complex<int16_t> i16cpx;

    class BlueFFT : public FFTModel {
    public:
        BlueFFT(int fft_size, int sample_rate);
        ~BlueFFT();
        virtual Spectrums makeSpectrum(const short* src);
        virtual Spectrums makeSpectrum(const float* src);
        int getResultSize() const;

    private:
        void initFloatSrc();
        static float shortToFloat(const short val);
        int frequencyToIndex(const int freq) const;
        template <typename T>
        void FFT(std::vector<T> &data, bool inv);

        std::vector<fcpx> floatSrc;
        std::vector<i16cpx> i16Src;
        std::vector<float> amplitudeSpectrum;
        std::vector<float> phaseSpectrum;
        int fftSize;
        int halfOfFFTSize;
    };
}

#endif //EUPHONY_BLUEFFT_H
