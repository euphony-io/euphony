#ifndef EUPHONY_BLUEFFT_H
#define EUPHONY_BLUEFFT_H

#include "FFTModel.h"
#include <complex>
#include <vector>

namespace Euphony {

    constexpr double PI = 3.14159265358979323846264338327;
    typedef std::complex<float> cpx;

    class BlueFFT : public FFTModel {
    public:
        BlueFFT(int fft_size, int sample_rate);
        ~BlueFFT();
        virtual float* makeSpectrum(short* src);
        virtual float* makeSpectrum(float* src);
        int getResultSize() const;

    private:
        static inline float shortToFloat(const short val);
        inline int frequencyToIndex(const int freq) const;
        void FFT(std::vector<cpx> &data, bool inv);

        /* unique_ptr's array version. it is available on c++14. */
        std::vector<cpx> spectrum;
        std::vector<float> result;
        int fftSize;
        int halfOfFFTSize;
    };
}

#endif //EUPHONY_BLUEFFT_H
