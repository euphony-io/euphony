#include "../WakeUpFFTSensor.h"
#include <FFTHelper.h>
#include <FSK.h>
#include <FFTProcessor.h>

using namespace Euphony;

WakeUpFFTSensor::WakeUpFFTSensor(int sampleRate)
: preFFTSize(32), postFFTSize(512), isStarted(false), sampleRate(sampleRate)
{
    preFFT = std::make_unique<FFTProcessor>(preFFTSize, sampleRate);
    postFFT = std::make_unique<FFTProcessor>(postFFTSize, sampleRate);
}


bool WakeUpFFTSensor::detectWakeUpSign(const float *audioSrc, const int size) {
    std::vector<int> resultVector(32, 0);

    int detectStartPos = isWaveDetected(audioSrc, size);
    if(detectStartPos != -1)
        return (isStartSignalDetected(audioSrc + detectStartPos, size) > 0);

    return false;
}


int WakeUpFFTSensor::isStartSignalDetected(const float *audioSrc, const int size) {
    int startSignalCount = 0;
    for(int i = 0; i < size; i += postFFTSize) {
        auto result = postFFT->makeSpectrum(audioSrc + i);
        if(FFTHelper::getMaxIdxFromSource(result.amplitudeSpectrum, kStandardFrequency, 32, postFFTSize, sampleRate) != -1) {
            continue;
        }

        startSignalCount++;
    }

    return startSignalCount;
}


int WakeUpFFTSensor::isWaveDetected(const float* audioSrc, const int size) {

    int waveCount = 0;
    for(int i = 0; i < size; i += preFFTSize) {
        auto result = preFFT->makeSpectrum(audioSrc + i);
        const int idx = FFTHelper::getMaxIdxFromSource(result.amplitudeSpectrum, kStandardFrequency, 2, preFFTSize, sampleRate);

        if(idx != 0) {
            waveCount = 0;
            continue;
        }

        waveCount++;
    }

    return (waveCount == 0) ? -1 : size - (waveCount * preFFTSize);
}


bool WakeUpFFTSensor::isWakeUp() {
    return isStarted;
}

bool WakeUpFFTSensor::isWakeUp(int signFrequency) {
    return false;
}
