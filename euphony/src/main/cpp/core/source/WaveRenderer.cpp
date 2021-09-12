#include <utility>

#include "../WaveRenderer.h"

using namespace Euphony;

WaveRenderer::WaveRenderer(WaveList waveListSrc, int32_t channelCountSrc)
: channelCount (channelCountSrc)
, waveSourceSize(0)
, readFrameIndex(0)
, renderIndex(0)
, renderTotalCount(0)
{
    setWaveList(std::move(waveListSrc));
}

void WaveRenderer::renderAudio(float *targetData, int32_t numFrames) {
    if(isWaveOn) {
        int64_t framesToRenderFromData = numFrames;
        const float *waveSrcData = waveSource.get();

        if (readFrameIndex + numFrames >= waveSourceSize) {
            framesToRenderFromData = waveSourceSize - readFrameIndex;
        }

        for (int i = 0; i < framesToRenderFromData; ++i) {
            for (int j = 0; j < channelCount; ++j) {
                targetData[(i * channelCount) + j] = waveSrcData[readFrameIndex];
            }
            if (++readFrameIndex >= waveSourceSize){
                readFrameIndex = 0;
                if(renderTotalCount > 0) {
                    if(++renderIndex >= renderTotalCount)
                        isWaveOn.store(false);
                }
            }
        }

        if(framesToRenderFromData < numFrames){
            renderSilence(&targetData[framesToRenderFromData], numFrames * channelCount);
        }

    } else {
        renderSilence(targetData, numFrames * channelCount);
    }
}

void WaveRenderer::tap(bool isDown) {
    isWaveOn.store(isDown);
}

void WaveRenderer::tapCount(bool isDown, int count) {
    isWaveOn.store(isDown);
    renderIndex = 0;
    renderTotalCount = count;
}

float* WaveRenderer::getWaveSource() {
    return waveSource.get();
}

int32_t WaveRenderer::getWaveSourceSize() const {
    return waveSourceSize;
}

void WaveRenderer::renderSilence(float *targetData, int32_t numFrames) {
    for(int i = 0; i < numFrames; ++i) {
        targetData[i] = 0;
    }
}

void WaveRenderer::setWaveList(WaveList waveListSrc) {
    waveSourceSize = waveListSrc.size() * kBufferSize;
    waveSource = std::make_unique<float[]>(waveSourceSize);
    std::fill_n(waveSource.get(), waveSourceSize, 0);
    for(int i = 0; i < waveListSrc.size(); i++) {
        auto waveSrc = waveListSrc[i]->getSource();
        for(int j = 0; j < kBufferSize; j++) {
            waveSource[j + (i * kBufferSize)] = waveSrc[j];
        }
    }
}
