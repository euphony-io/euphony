#include <utility>

#include "WaveRenderer.h"

using namespace Euphony;

std::shared_ptr<WaveRenderer> WaveRenderer::instance = nullptr;
std::once_flag WaveRenderer::flag;

WaveRenderer::WaveRenderer()
: channelCount (kChannelCount)
, waveSourceSize(0)
, readFrameIndex(0)
, renderIndex(0)
, renderTotalCount(0){ }

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
        const float *waveSrcData = waveSource.get();

        for (int i = 0; i < numFrames; ++i) {
            const int targetDataIndex = i * channelCount;
            for (int chNo = 0; chNo < channelCount; ++chNo) {
                targetData[targetDataIndex + chNo] = waveSrcData[readFrameIndex];
            }
            if (++readFrameIndex == waveSourceSize){
                readFrameIndex = 0;
                if(renderTotalCount > 0 && ++renderIndex == renderTotalCount) {
                    for (int j = i + 1; j < numFrames; ++j) {
                        for (int chNo = 0; chNo < channelCount; ++chNo) {
                            targetData[j + chNo] = 0;
                        }
                    }
                    isWaveOn.store(false);
                    break;
                }
            }
        }
    } else {
        renderIndex = 0;
        readFrameIndex = 0;
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

std::shared_ptr<WaveRenderer> WaveRenderer::getInstance() {
    std::call_once(WaveRenderer::flag, []() {
        WaveRenderer::instance.reset(new WaveRenderer());
    });

    return instance;
}
