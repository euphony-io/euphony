#include "BlueFFT.h"
#include <Definitions.h>

using namespace Euphony;

BlueFFT::BlueFFT(int fft_size)
: FFTModel(fft_size)
, fftSize(fft_size)
, halfOfFFTSize(fft_size >> 1)
{
    floatSrc.resize(fft_size);
    i16Src.resize(fft_size);
    amplitudeSpectrum.resize(halfOfFFTSize);
    phaseSpectrum.resize(halfOfFFTSize);
}

BlueFFT::~BlueFFT() {
    std::vector<fcpx>().swap(floatSrc);
    std::vector<i16cpx>().swap(i16Src);
    std::vector<float>().swap(amplitudeSpectrum);
    std::vector<float>().swap(phaseSpectrum);
}

void BlueFFT::initialize() {
    std::vector<fcpx>().swap(floatSrc);
    floatSrc.resize(fftSize, 0);

    std::vector<i16cpx>().swap(i16Src);
    i16Src.resize(fftSize, 0);

    std::vector<float>().swap(amplitudeSpectrum);
    amplitudeSpectrum.resize(halfOfFFTSize, 0);

    std::vector<float>().swap(phaseSpectrum);
    phaseSpectrum.resize(halfOfFFTSize, 0);
}

Spectrums BlueFFT::makeSpectrum(const short *src) {
    /* TODO: should implement makeSpectrum for short source */
    return {nullptr, nullptr};
}

Spectrums BlueFFT::makeSpectrum(const float *src) {
    initialize();

    for(int i = 0; i < fftSize; i++) {
        floatSrc[i].real(src[i]);
    }

    FFT(floatSrc, false);

    int startIdx = 0;
    int lenHalfOfNumSamples = halfOfFFTSize;

    for(int i = startIdx; i <= lenHalfOfNumSamples; ++i) {
        float re = floatSrc[i].real() * (float)fftSize;
        float im = floatSrc[i].imag() * (float)fftSize;

        amplitudeSpectrum[i] = makeAmplitudeSpectrum(re, im);
        phaseSpectrum[i] = makePhaseSpectrum(re, im);
    }

    return {&amplitudeSpectrum[0], &phaseSpectrum[0]};
}

template <typename T>
void BlueFFT::FFT(std::vector<T> &spectrum, bool inv) {
    int N = fftSize;
    for(int i=1, j=0; i<N; i++) {
        int bit = N >> 1;

        while(j & bit) {
            j ^= bit;
            bit >>= 1;
        }
        j ^= bit;

        if(i < j)
            swap(spectrum[i], spectrum[j]);
    }

    for(int k=1; k<N; k<<=1) {
        double angle = (inv ? PI/k : -PI/k);
        T dir(cos(angle), sin(angle));

        for(int i=0; i<N; i+=(k<<1)) {
            T unit(1, 0);

            for(int j=0; j<k; j++) {
                T u = spectrum[i+j];
                T v = spectrum[i+j+k] * unit;

                spectrum[i+j] = u + v;
                spectrum[i+j+k] = u - v;

                unit *= dir;
            }
        }
    }

    if(inv)
        for(int i=0; i<N; i++) spectrum[i] /= N;
}
