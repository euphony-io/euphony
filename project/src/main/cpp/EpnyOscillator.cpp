//
// Created by designe on 20. 8. 25.
//

#include "EpnyOscillator.h"

/*
 * Frequencies Methods
 * */
void EpnyOscillator::setFrequency(double frequency) {
    mFrequency = frequency;
    mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
}

void EpnyOscillator::setSampleRate(int32_t sampleRate) {
    mSampleRate = sampleRate;
    mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
}

void EpnyOscillator::setWaveOn(bool isWaveOn) {
    mIsWaveOn.store(isWaveOn);
}

void EpnyOscillator::renderAudio(float *data, int32_t numFrames) {
    if(mIsWaveOn) {
        //int crossFadeRange = numFrames / 4;
        for(int i = 0; i < numFrames; ++i) {
            data[i] = (float) (sin(mPhase) * mAmplitude);
            mPhase += mPhaseIncrement;
            if(mPhase > kTwoPi) mPhase -= kTwoPi;
        }

    } else {
        memset(data, 0, sizeof(float) * numFrames);
    }
}
