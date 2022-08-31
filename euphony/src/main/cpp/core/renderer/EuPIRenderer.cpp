//
// Created by desig on 2020-08-15.
//

#include "EuPIRenderer.h"

using namespace Euphony;

std::shared_ptr<EuPIRenderer> EuPIRenderer::instance = nullptr;
std::once_flag EuPIRenderer::flag;

EuPIRenderer::EuPIRenderer(int32_t sampleRate, int32_t channelCount)
        : mSampleRate(sampleRate)
        , mChannelCount(channelCount)
        , mOscillators(std::make_unique<EuPIOscillator[]>(channelCount))
        , mBuffer(std::make_unique<float[]>(kBufferSize)) {

    constexpr float amplitude = 1.0;

    // Set up the oscillators
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setSampleRate(mSampleRate);
        mOscillators[i].setFrequency(kStandardFrequency);
        mOscillators[i].setAmplitude(amplitude);
    }
}

void EuPIRenderer::renderAudio(float *audioData, int32_t numFrames) {
    // Render each oscillator into its own channel
    std::fill_n(mBuffer.get(), kBufferSize, 0);
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].renderAudio(mBuffer.get(), numFrames);
        for (int j = 0; j < numFrames; ++j) {
            audioData[(j * mChannelCount) + i] = mBuffer[j];
        }
    }
}

void EuPIRenderer::setFrequency(double frequency) {
    mOscillators = std::make_unique<EuPIOscillator[]>(mChannelCount);
    for(int i = 0;  i< mChannelCount; ++i) {
        mOscillators[i].setFrequency(frequency);
    }
}

void EuPIRenderer::tap(bool isDown) {
    for (int i = 0; i < mChannelCount; ++i) {
        mOscillators[i].setWaveOn(isDown);
    }
}

std::unique_ptr<float[]> EuPIRenderer::makeStaticWave(int freq) {
    std::unique_ptr<float[]> waveArray = std::make_unique<float[]>(kBufferSize);
    float phase = 0.0;
    double phaseIncrement = (kTwoPi * freq) / static_cast<double>(kSampleRate);
    for (int i = 0; i < kBufferSize; i++) {
        waveArray[i] = sin(phase);
        phase += phaseIncrement;
        if (phase > kTwoPi) phase -= kTwoPi;
    }

    return waveArray;
}

std::shared_ptr<EuPIRenderer> EuPIRenderer::getInstance(int32_t sampleRate, int32_t channelCount) {
    std::call_once(EuPIRenderer::flag, [](int32_t sr, int32_t cc) {
        EuPIRenderer::instance.reset(new EuPIRenderer(sr, cc));
    }, sampleRate, channelCount);

    return instance;
}
