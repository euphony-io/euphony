#include "FFTProcessor.h"
#include <Definitions.h>

using namespace Euphony;

FFTProcessor::FFTProcessor(int fft_size)
: FFTModel(fft_size)
, fftSize(fft_size)
, config(nullptr)
, spectrum(nullptr)
, amplitudeSpectrum(nullptr)
, phaseSpectrum(nullptr)
, halfOfFFTSize(fft_size >> 1)
{
    config = kiss_fftr_alloc(fft_size, 0, nullptr, nullptr);
    spectrum = (kiss_fft_cpx*) malloc(sizeof(kiss_fft_cpx) * fft_size);
    amplitudeSpectrum = new float[halfOfFFTSize]();
    phaseSpectrum = new float[halfOfFFTSize]();
}

FFTProcessor::~FFTProcessor() {
    free(config);
    free(spectrum);
    delete[] amplitudeSpectrum;
    delete[] phaseSpectrum;
}

void FFTProcessor::initialize() {
    // initialize config
    free(config);
    config = kiss_fftr_alloc(fftSize, 0, nullptr, nullptr);

    // initialize spectrum
    for(int i = 0; i < fftSize; i++)
        spectrum[i] = {0, 0};

    // intialize amplitude & phase spectrum
    for(int i = 0; i < halfOfFFTSize; i++) {
        amplitudeSpectrum[i] = 0;
        phaseSpectrum[i] = 0;
    }
}

Spectrums FFTProcessor::makeSpectrum(const short* src) {
    /* TODO: should implement makeSpectrum for int16 source */
    return {nullptr, nullptr};
}


Spectrums FFTProcessor::makeSpectrum(const float *src) {
    initialize();

    int startIdx = 0;
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

Spectrums FFTProcessor::makeSpectrum(float *src, int numSamples) {

    return {nullptr, nullptr};
}

