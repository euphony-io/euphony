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
    amplitudeSpectrum = new float[halfOfFFTSize]();
    phaseSpectrum = new float[halfOfFFTSize]();
}

Euphony::FFTProcessor::~FFTProcessor() {
    free(config);
    free(spectrum);
    delete amplitudeSpectrum;
    delete phaseSpectrum;
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

Euphony::Spectrums Euphony::FFTProcessor::makeSpectrum(const short* src) {
    /* TODO: should implement makeSpectrum for int16 source */
    return {0, 0};
}


Euphony::Spectrums Euphony::FFTProcessor::makeSpectrum(const float *src) {

    int startIdx = frequencyToIndex(kStartSignalFrequency) - 1;
    int lenHalfOfNumSamples = halfOfFFTSize; // spectrum size must be half of numSamples;

    kiss_fftr(config, src, spectrum);

    for(int i = startIdx; i <= lenHalfOfNumSamples; ++i) {
        float re = spectrum[i].r * (float)fftSize;
        float im = spectrum[i].i * (float)fftSize;

        amplitudeSpectrum[i] = makeAmplitudeSpectrum(re, im);
        phaseSpectrum[i] = makePhaseSpectrum(re, im);

    }

    return {&amplitudeSpectrum[0], &phaseSpectrum[0]};
}

Euphony::Spectrums Euphony::FFTProcessor::makeSpectrum(float *src, int numSamples) {

    return {nullptr, nullptr};
}

int Euphony::FFTProcessor::getResultSize() const {
    return halfOfFFTSize + 1;
}

