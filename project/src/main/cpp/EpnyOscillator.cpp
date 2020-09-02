//
// Created by designe on 20. 8. 25.
//

#include "EpnyOscillator.h"

void EpnyOscillator::setFrequency(double frequency) {
    mFrequency = frequency;
}

void EpnyOscillator::setSampleRate(int32_t sampleRate) {
    mSampleRate = sampleRate;
}

void EpnyOscillator::setWaveOn(bool isWaveOn) {
    mIsWaveOn.store(isWaveOn);
}

void EpnyOscillator::renderAudio(float *data, int32_t numFrames) {
    if(mIsWaveOn) {
        double time = 0.0;
        for(int i = 0; i < numFrames; ++i) {
            time = (double)i / (double)mSampleRate;
            data[i] = sinf(kTwoPi * mFrequency * time) * mAmplitude;
        }

    } else {
        memset(data, 0, sizeof(float) * numFrames);
    }
}
