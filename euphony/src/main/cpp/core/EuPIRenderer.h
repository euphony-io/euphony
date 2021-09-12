//
// Created by desig on 2020-08-15.
//

#ifndef EUPHONY_EUPIRENDERER_H
#define EUPHONY_EUPIRENDERER_H


#include "EuPIOscillator.h"
#include "EuphonyAudioSource.h"

namespace Euphony {
    class EuPIRenderer : public EuphonyAudioSource {
    public:
        EuPIRenderer(int32_t sampleRate, int32_t channelCount);
        EuPIRenderer(EuPIRenderer &&other) = default;
        EuPIRenderer &operator=(EuPIRenderer &&other) = default;
        ~EuPIRenderer() = default;

        void tap(bool isDown) override;
        void renderAudio(float *audioData, int32_t numFrames) override; // from IRenderableAudio
        void setFrequency(double frequency);
        std::unique_ptr<float[]> makeStaticWave(int freq);

    private:
        std::unique_ptr<EuPIOscillator[]> mOscillators;
        std::unique_ptr<float[]> mBuffer = std::make_unique<float[]>(kBufferSize);
        int32_t mChannelCount;
        int32_t mSampleRate;
    };
}


#endif //EUPHONY_EUPIRENDERER_H
