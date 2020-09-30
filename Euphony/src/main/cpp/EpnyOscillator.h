//
// Created by designe on 20. 8. 25.
//

#ifndef EUPHONY_EPNYOSCILLATOR_H
#define EUPHONY_EPNYOSCILLATOR_H

#include <cstdint>
#include <atomic>
#include <math.h>
#include <memory>
#include <vector>
#include <IRenderableAudio.h>

constexpr int32_t kDefaultSampleRate = 48000;
constexpr double kPi = M_PI;
constexpr double kTwoPi = kPi * 2.0;

class EpnyOscillator : public IRenderableAudio {
public:
    ~EpnyOscillator() = default;
    void setWaveOn(bool isWaveOn);
    void setSampleRate(int32_t sampleRate);
    void setFrequency(double frequency);
    inline void setAmplitude(float amplitude) {
        mAmplitude = amplitude;
    }
    // From IRenderableAudio
    void renderAudio(float *data, int32_t numFrames);

private:
    std::atomic<bool> mIsFirstWave { false };
    std::atomic<bool> mIsLastWave { false };
    std::atomic<bool> mIsWaveOn { false };
    float mPhase = 0.0;
    std::atomic<float> mAmplitude { 0 };
    std::atomic<double> mPhaseIncrement { 0.0 };
    double mFrequency = 0.0;
    std::vector<double> mTimeArray;
    int32_t mSampleRate = kDefaultSampleRate;

    void updatePhaseIncrement() {
        mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
    }
};


#endif //EUPHONY_EPNYOSCILLATOR_H
