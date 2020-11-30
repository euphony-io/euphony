//
// Created by opener on 20. 11. 30.
//

#include "EpnyFFTProcessor.h"

EpnyFFTProcessor::EpnyFFTProcessor(int fft_size) {
    create(fft_size);
}

EpnyFFTProcessor::~EpnyFFTProcessor() {
    destroy();
}

void EpnyFFTProcessor::create(int fft_size) {
    mFFT = std::make_unique<KissFFT>();

    mFFT->config = kiss_fftr_alloc(fft_size, 0, nullptr, nullptr);
    mFFT->spectrum = std::make_unique<kiss_fft_cpx[]>(fft_size);
    mFFT->numSamples = fft_size;
}

void EpnyFFTProcessor::destroy() {
    free(mFFT->config);
    mFFT->spectrum.reset();
    mFFT.reset();
    mFFT = nullptr;
}

void EpnyFFTProcessor::doSpectrum() {

}

void EpnyFFTProcessor::doSpectrums() {

}
