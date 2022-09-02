//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EUPIRENDERER_H
#define EUPHONY_EUPIRENDERER_H


#include <mutex>
#include "EuPIOscillator.h"
#include "EuphonyAudioSource.h"

namespace Euphony {
    class EuPIRenderer : public EuphonyAudioSource {
    public:
        EuPIRenderer(EuPIRenderer &&) = delete;
        EuPIRenderer &operator=(const EuPIRenderer &) = delete;
        EuPIRenderer &operator=(EuPIRenderer &&) = delete;
        ~EuPIRenderer() = default;

        static std::shared_ptr<EuPIRenderer> getInstance(int32_t sampleRate, int32_t channelCount);

        void tap(bool isDown) override;
        void renderAudio(float *audioData, int32_t numFrames) override; // from IRenderableAudio
        void setFrequency(double frequency);
        std::unique_ptr<float[]> makeStaticWave(int freq);

    private:
        EuPIRenderer() = default;
        EuPIRenderer(int32_t sampleRate, int32_t channelCount);
        static std::shared_ptr<EuPIRenderer> instance;
        static std::once_flag flag;

        std::unique_ptr<EuPIOscillator[]> mOscillators;
        std::unique_ptr<float[]> mBuffer;
        int32_t mChannelCount;
        int32_t mSampleRate;
    };
}


#endif //EUPHONY_EUPIRENDERER_H
