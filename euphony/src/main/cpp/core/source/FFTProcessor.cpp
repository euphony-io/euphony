//
// Created by opener on 20. 11. 30.
//

#include "../FFTProcessor.h"
#include "../Definitions.h"

Euphony::FFTProcessor::FFTProcessor(int fft_size, int samplerate)
: FFTModel(fft_size, samplerate)
, fftSize(fft_size)
, halfOfFFTSize(fft_size >> 1)
{
    config = kiss_fftr_alloc(fft_size, 0, nullptr, nullptr);
    spectrum = (kiss_fft_cpx*) malloc(sizeof(kiss_fft_cpx) * (int)fft_size);
    result = new float[halfOfFFTSize]();
}

Euphony::FFTProcessor::~FFTProcessor() {
    free(config);
    free(spectrum);
    delete result;
}

inline float Euphony::FFTProcessor::shortToFloat(const short val) {
    if( val < 0 )
        return val * ( 1 / 32768.0f );
    else
        return val * ( 1 / 32767.0f );
}


inline int Euphony::FFTProcessor::frequencyToIndex(const int freq) const {
    return (int)(((halfOfFFTSize) + 1) * ((double)freq / (double)getSampleRate()));
}

float* Euphony::FFTProcessor::makeSpectrum(short* src) {
    int startIdx = frequencyToIndex(kStartSignalFrequency) - 1;
    int lenHalfOfNumSamples = halfOfFFTSize; // spectrum size must be half of numSamples;

    kiss_fftr(config, src, spectrum);

    for(int i = startIdx; i <= lenHalfOfNumSamples; ++i) {
        float re = shortToFloat(spectrum[i].r) * (float)fftSize;
        float im = shortToFloat(spectrum[i].i) * (float)fftSize;

        result[i] = sqrtf(re * re + im * im) / (float)lenHalfOfNumSamples;
    }

    return result;
}

int Euphony::FFTProcessor::getResultSize() const {
    return halfOfFFTSize + 1;
}
