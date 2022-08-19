#include "../FFTHelper.h"
#include <cmath>

using namespace Euphony;

FFTHelper::FFTHelper(const int fftSize, const int sampleRate, const int standardFrequency)
: fftSize(fftSize)
, sampleRate(sampleRate)
, standardFrequency(standardFrequency)
{}

int FFTHelper::getIndexOfStandardFrequency() const {
    return FFTHelper::getIndexOfStandardFrequency(standardFrequency, fftSize, sampleRate);
}

int FFTHelper::getIndexOfStandardFrequency(const int standardFrequency, const int fftSize, const int sampleRate) {
    return std::lround((static_cast<float>(static_cast<float>(standardFrequency) / static_cast<float>(sampleRate >> 1)) * static_cast<float>(fftSize >> 1)));
}

int FFTHelper::getIndexOfEndFrequency(const int range) const {
    return this->getIndexOfStandardFrequency() + range - 1;
}

int FFTHelper::getIndexOfEndFrequency(const int standardFrequency, const int range, const int fftSize, const int sampleRate) {
    return FFTHelper::getIndexOfStandardFrequency(standardFrequency, fftSize, sampleRate) + range - 1;
}

int FFTHelper::getIndexOfFrequency(int frequency) const {
    return FFTHelper::getIndexOfFrequency(frequency, fftSize, sampleRate);
}

int FFTHelper::getIndexOfFrequency(const int frequency, const int fftSize, const int sampleRate) {
    return std::lround((static_cast<float>(static_cast<float>(frequency) / static_cast<float>(sampleRate >> 1)) * static_cast<float>(fftSize >> 1)));
}

int FFTHelper::getMaxIdxFromSource(const float *fft_source, const int standardFrequency, const int range, const int fftSize,
                                   const int sampleRate) {
    int maxIndex = 0;
    float maxValue = 0;
    const int startIdx = getIndexOfFrequency(standardFrequency, fftSize, sampleRate);
    const int endIdx = startIdx + range;
    for(int i = startIdx - 1; i < endIdx; i++) {
        if(fft_source[i] > maxValue) {
            maxValue = fft_source[i];
            maxIndex = i;
        }
    }

    return maxIndex - startIdx;
}

