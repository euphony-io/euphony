#ifndef EUPHONY_WAVERENDERER_H
#define EUPHONY_WAVERENDERER_H

#include "Definitions.h"
#include "EuphonyAudioSource.h"

namespace Euphony {

    class WaveRenderer : public EuphonyAudioSource{
    public:
        WaveRenderer(WaveList waveListSrc, int32_t channelCountSrc);
        ~WaveRenderer() = default;

        void renderAudio(float *targetData, int32_t numFrames) override; //From IRenderableAudio
        void tap(bool isDown) override;
        void tapCount(bool isDown, int count);
        float* getWaveSource();
        int32_t getWaveSourceSize() const;
        void setWaveList(WaveList waveListSrc);

    private:
        void renderSilence(float *targetData, int32_t numFrames);
        std::unique_ptr<float[]> waveSource;
        std::atomic<bool> isWaveOn { false };
        int32_t channelCount;
        int32_t readFrameIndex;
        int32_t waveSourceSize;
        int32_t renderIndex;
        int32_t renderTotalCount;
    };
}
#endif //EUPHONY_WAVERENDERER_H
