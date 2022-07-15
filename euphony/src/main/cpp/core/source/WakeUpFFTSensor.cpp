#include "../WakeUpFFTSensor.h"
#include <FSK.h>

using namespace Euphony;

WakeUpFFTSensor::WakeUpFFTSensor(int sampleRate)
: gear1stFFTSize(32), gear2ndFFTSize(32)
{
    gear1st = std::make_unique<FFTProcessor>(gear1stFFTSize, sampleRate);
    gear2nd = std::make_uqniue<FFTProcessor>(gear2ndFFTSize, sampleRate);
}


int WakeUpFFTSensor::feedAudioData(float *audioSrc, int size) {
    for(int i = 0; i < size; i += gear1stFFTSize) {
        auto result = gear1st->makeSpectrum(audioSrc + i);
        FSK::getMaxIdxFromSource(result.amplitudeSpectrum, 2, sampleRate, gear1stFFTSize)
    }

    return 0;
}

bool WakeUpFFTSensor::isWakeUp() {
    return isStarted;
}

bool WakeUpFFTSensor::isWakeUp(int signFrequency) {
    return false;
}

