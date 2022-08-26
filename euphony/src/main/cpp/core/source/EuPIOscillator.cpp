//
// Created by designe on 20. 8. 25.
//

#include "../EuPIOscillator.h"

/*
 * Frequencies Methods
 * */
void Euphony::EuPIOscillator::setFrequency(double frequency) {
    mFrequency = frequency;
    mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
}

void Euphony::EuPIOscillator::setSampleRate(int32_t sampleRate) {
    mSampleRate = sampleRate;
    mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
}

void Euphony::EuPIOscillator::setWaveOn(bool isWaveOn) {
    mIsWaveOn.store(isWaveOn);
}

void Euphony::EuPIOscillator::renderAudio(float *data, int32_t numFrames) {
    /* mapping frequencies data */
    for(int i = 0; i < numFrames; ++i) {
        data[i] = sin(mPhase) * mAmplitude;
        mPhase += mPhaseIncrement;
        if (mPhase > kTwoPi) mPhase -= kTwoPi;
    }

    if(mIsWaveOn) {
        if(mIsFirstWave != true) {
            /* Crossfade first */
            for(int i = 0; i < numFrames; i++) {
                data[i] *= ((float)i / (float)numFrames);
            }
            mIsFirstWave.store(true);
        }
    }
}
