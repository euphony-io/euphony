//
// Created by desig on 2020-08-15.
//

#include "WaveGenerator.h"

Euphony::WaveGenerator::WaveGenerator(int32_t sampleRate, int32_t channelCount) :
        TappableAudioSource(sampleRate, channelCount)
        , mOscillators(std::make_unique<Oscillator[]>(channelCount)){

    double frequency = 18000.0;
    //constexpr double interval = 1000.0;
    constexpr float amplitude = 1.0;

    // Set up the oscillators
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setFrequency(frequency);
        mOscillators[i].setSampleRate(mSampleRate);
        mOscillators[i].setAmplitude(amplitude);
      //  frequency += interval;
    }
}

void Euphony::WaveGenerator::renderAudio(float *audioData, int32_t numFrames) {
    // Render each oscillator into its own channel
    std::fill_n(mBuffer.get(), kSharedBufferSize, 0);
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].renderAudio(mBuffer.get(), numFrames);
        for (int j = 0; j < numFrames; ++j) {
            audioData[(j * mChannelCount) + i] = mBuffer[j];
        }
    }
}

void Euphony::WaveGenerator::setFrequency(double frequency) {
    for(int i = 0;  i< mChannelCount; ++i) {
        mOscillators[i].setFrequency(frequency);
    }
}

void Euphony::WaveGenerator::tap(bool isDown) {
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setWaveOn(isDown);
    }
}

std::unique_ptr<float[]> Euphony::WaveGenerator::makeStaticWave(int freq) {
    std::unique_ptr<float[]> waveArray = std::make_unique<float[]>(kDataLength);
    float phase = 0.0;
    double phaseIncrement = (kTwoPi * freq) / static_cast<double>(kSampleRate);
    for (int i = 0; i < kDataLength; i++) {
        waveArray[i] = (float) sin(phase);
        phase += phaseIncrement;
        if (phase > kTwoPi) phase -= kTwoPi;
    }

    return waveArray;
}