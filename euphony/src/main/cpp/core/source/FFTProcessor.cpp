//
// Created by opener on 20. 11. 30.
//

#include "../FFTProcessor.h"
#include "../Definitions.h"

Euphony::FFTProcessor::FFTProcessor(int fft_size, int samplerate) {
    initialize(fft_size, samplerate);
}

Euphony::FFTProcessor::~FFTProcessor() {
    destroy();
}

inline float Euphony::FFTProcessor::shortToFloat(short val) {
    if( val < 0 )
        return val * ( 1 / 32768.0f );
    else
        return val * ( 1 / 32767.0f );
}


inline int Euphony::FFTProcessor::frequencyToIndex(int freq) {
    return ((fftSize>>1) + 1) * ((double)freq / (double)sampleRate);
}


void Euphony::FFTProcessor::initialize(int fft_size, int samplerate) {
    config = kiss_fftr_alloc(fft_size, 0, nullptr, nullptr);
    spectrum = (kiss_fft_cpx*) malloc(sizeof(kiss_fft_cpx) * (int)fft_size);
    result = new float[fft_size >> 1]();
    fftSize = fft_size;
    sampleRate = samplerate;
}

void Euphony::FFTProcessor::destroy() {
    free(config);
    free(spectrum);
    delete result;
}

float* Euphony::FFTProcessor::makeSpectrum(short* src) {
    int startIdx = frequencyToIndex(kStartSignalFrequency) - 1;
    int lenHalfOfNumSamples = fftSize >> 1; // spectrum size must be half of numSamples;

    kiss_fftr(config, src, spectrum);

    for(int i = startIdx; i <= lenHalfOfNumSamples; ++i) {
        float re = shortToFloat(spectrum[i].r) * fftSize;
        float im = shortToFloat(spectrum[i].i) * fftSize;

        result[i] = sqrtf( re * re + im * im) / lenHalfOfNumSamples;
    }

    return result;
}

int Euphony::FFTProcessor::getResultSize() const {
    return (fftSize >> 1) + 1;
}
