//
// Created by opener on 20. 11. 30.
//

#include "../FFTProcessor.h"

Euphony::FFTProcessor::FFTProcessor(int fft_size, int samplerate) {
    create(fft_size, samplerate);
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
    return ((mFFT->numSamples>>1) + 1) * ((double)freq / (double)mFFT->samplerate);
}


void Euphony::FFTProcessor::create(int fft_size, int samplerate) {
    mFFT = std::make_unique<KissFFT>();

    mFFT->config = kiss_fftr_alloc(fft_size, 0, nullptr, nullptr);
    mFFT->spectrum = new kiss_fft_cpx[fft_size];
    mFFT->result = new float[fft_size >> 1];
    mFFT->numSamples = fft_size;
    mFFT->samplerate = samplerate;
}

void Euphony::FFTProcessor::destroy() {
    free(mFFT->config);
    delete mFFT->spectrum;
    mFFT.reset();
    mFFT = nullptr;
}

float Euphony::FFTProcessor::doSpectrum(int spectrum_idx) {
    float re = shortToFloat(mFFT->spectrum[spectrum_idx].r) * mFFT->numSamples;
    float im = shortToFloat(mFFT->spectrum[spectrum_idx].i) * mFFT->numSamples;

    return sqrtf( re * re + im * im ) / (mFFT->numSamples / 2);
}

float* Euphony::FFTProcessor::doSpectrums(int from_idx, int to_idx, short* src) {
    float startFrequency = 17500.0;
    int spectrum_len = mFFT->numSamples >> 1; // spectrum size must be half of numSamples;
    //int start = from_idx;//(startFrequency / (float)mFFT->samplerate);

    kiss_fftr(mFFT->config, src, mFFT->spectrum);

    for(int i = from_idx; i <= to_idx; ++i) {
        float re = shortToFloat(mFFT->spectrum[i].r) * mFFT->numSamples;
        float im = shortToFloat(mFFT->spectrum[i].i) * mFFT->numSamples;

        mFFT->result[i] = sqrtf( re * re + im * im) / spectrum_len;
    }

    return mFFT->result;
}