//
// Created by designe on 20. 8. 25.
//

#ifndef EUPHONY_EUPIOSCILLATOR_H
#define EUPHONY_EUPIOSCILLATOR_H

#include "Definitions.h"
#include <cstdint>
#include <atomic>
#include <math.h>
#include <memory>
#include <vector>
#include <IRenderableAudio.h>

namespace Euphony {
    class EuPIOscillator : public IRenderableAudio {
    public:
        ~EuPIOscillator() = default;
        void setWaveOn(bool isWaveOn);
        void setSampleRate(int32_t sampleRate);
        void setFrequency(double frequency);
        inline void setAmplitude(float amplitude) {
            mAmplitude = amplitude;
        }
        // From IRenderableAudio
        void renderAudio(float *data, int32_t numFrames);
        static double getPhaseIncrement(double frequency){
            return ((kTwoPi * frequency) / static_cast<double> (kSampleRate));
        }
        static std::unique_ptr<float[]> makeStaticWave(int freq, int waveLength) {
            std::unique_ptr<float[]> source = std::make_unique<float[]>(waveLength);

            double phase = 0.0;
            double phaseIncrement = Euphony::EuPIOscillator::getPhaseIncrement(freq);
            for(int i = 0; i < waveLength; i++) {
                source[i] = sin(phase);
                phase += phaseIncrement;
                if(phase > kTwoPi) phase -= kTwoPi;
            }

            return std::unique_ptr<float[]>();
        }

    private:
        std::atomic<bool> mIsFirstWave{false};
        std::atomic<bool> mIsLastWave{false};
        std::atomic<bool> mIsWaveOn{false};
        double mPhase = 0.0;
        std::atomic<double> mAmplitude{1.0};
        std::atomic<double> mPhaseIncrement{0.0};
        double mFrequency = 0.0;
        int32_t mSampleRate = kSampleRate;

        void updatePhaseIncrement() {
            mPhaseIncrement.store((kTwoPi * mFrequency) / static_cast<double>(mSampleRate));
        }
    };
}

#endif //EUPHONY_EUPIOSCILLATOR_H
