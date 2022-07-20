#include "../WakeUpFFTSensor.h"
#include <FSK.h>
#include <FFTProcessor.h>

using namespace Euphony;

WakeUpFFTSensor::WakeUpFFTSensor(int sampleRate)
: preFFTSize(32), postFFTSize(2048), isStarted(false), sampleRate(sampleRate)
{
    preFFT = std::make_unique<FFTProcessor>(preFFTSize, sampleRate);
    postFFT = std::make_unique<FFTProcessor>(postFFTSize, sampleRate);
}


int WakeUpFFTSensor::feedAudioData(const float *audioSrc, const int size) {
    std::vector<int> resultVector(32, 0);

    return isWaveDetected(audioSrc, size);
}

int WakeUpFFTSensor::isWaveDetected(const float* audioSrc, const int size) {
    int resultArray[4] = {0, };

    int maxIdx = 0, maxIdxCount = 0;
    int waveCount = 0;
    for(int i = 0; i < size; i += preFFTSize) {
        auto result = preFFT->makeSpectrum(audioSrc + i);
        const int idx = FSK::getMaxIdxFromSource(result.amplitudeSpectrum, 2, sampleRate, preFFTSize);

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

