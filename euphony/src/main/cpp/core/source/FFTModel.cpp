#include "../FFTModel.h"
#include "../Definitions.h"

using namespace Euphony;

FFTModel::FFTModel(int fftSizeSrc, int sampleRateSrc)
: fftSize(fftSizeSrc)
, sampleRate(sampleRateSrc)
{ }

int FFTModel::getFFTSize() const {
    return fftSize;
}

void FFTModel::setFFTSize(int fftSizeSrc) {
    fftSize = fftSizeSrc;
}

int FFTModel::getSampleRate() const {
    return sampleRate;
}

void FFTModel::setSampleRate(int sampleRateSrc) {
    sampleRate = sampleRateSrc;
}

