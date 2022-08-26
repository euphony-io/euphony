#ifndef EUPHONY_WAVERENDERER_H
#define EUPHONY_WAVERENDERER_H

#include <mutex>
#include "Definitions.h"
#include "EuphonyAudioSource.h"

namespace Euphony {

    class WaveRenderer : public EuphonyAudioSource{
    public:
        WaveRenderer(const WaveRenderer &) = delete;
        WaveRenderer(WaveRenderer &&) = delete;
        WaveRenderer &operator=(const WaveRenderer &) = delete;
        WaveRenderer &operator=(WaveRenderer &&) = delete;
        ~WaveRenderer() = default;

        static std::shared_ptr<WaveRenderer> getInstance();

        void renderAudio(float *targetData, int32_t numFrames) override; //From IRenderableAudio
        void tap(bool isDown) override;
        void tapCount(bool isDown, int count);
        float* getWaveSource();
        int32_t getWaveSourceSize() const;
        void setWaveList(WaveList waveListSrc);

    private:
        WaveRenderer();
        WaveRenderer(WaveList waveListSrc, int32_t channelCountSrc);
        static std::shared_ptr<WaveRenderer> instance;
        static std::once_flag flag;

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
