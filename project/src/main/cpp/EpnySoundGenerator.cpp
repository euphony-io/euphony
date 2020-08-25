//
// Created by desig on 2020-08-15.
//

#include "EpnySoundGenerator.h"

EpnySoundGenerator::EpnySoundGenerator(int32_t sampleRate, int32_t channelCount) :
        TappableAudioSource(sampleRate, channelCount)
        , mOscillators(std::make_unique<EpnyOscillator[]>(channelCount)){

    double frequency = 440.0;
    constexpr double interval = 110.0;
    constexpr float amplitude = 1.0;

    // Set up the oscillators
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setFrequency(frequency);
        mOscillators[i].setSampleRate(mSampleRate);
        mOscillators[i].setAmplitude(amplitude);
        frequency += interval;
    }
}

void EpnySoundGenerator::renderAudio(float *audioData, int32_t numFrames) {
    // Render each oscillator into its own channel
    std::fill_n(mBuffer.get(), kSharedBufferSize, 0);
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].renderAudio(mBuffer.get(), numFrames);
        for (int j = 0; j < numFrames; ++j) {
            audioData[(j * mChannelCount) + i] = mBuffer[j];
        }
    }
}

void EpnySoundGenerator::tap(bool isDown) {
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setWaveOn(isDown);
    }
}
