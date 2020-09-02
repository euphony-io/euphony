//
// Created by desig on 2020-08-15.
//
#include <oboe/Oboe.h>
#include <Log.h>
#include "EpnyTxEngine.h"
#include "EpnySoundGenerator.h"
#include "EpnyAudioStreamCallback.h"

class EpnyTxEngine::Impl : public IRestartable{
public:
    std::mutex mLock;
    std::shared_ptr<oboe::AudioStream> mStream;
    oboe::AudioStreamBuilder mStreamBuilder;
    std::unique_ptr<EpnyAudioStreamCallback> mCallback;
    std::shared_ptr<EpnySoundGenerator> mAudioSource;
    bool mIsLatencyDetectionSupported = false;

    int32_t mDeviceId = oboe::Unspecified;
    int32_t mChannelCount = oboe::Unspecified;
    EpnyStatus mStatus = STOP;
    oboe::AudioApi mAudioApi = oboe::AudioApi::Unspecified;


    Impl() {
        createCallback();
        start();
    }

    virtual ~Impl() = default;

    void createCallback() {
        mCallback = std::make_unique<EpnyAudioStreamCallback>(*this);
    }

    oboe::Result createPlaybackStream() {
        return mStreamBuilder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(mCallback.get())
                ->setChannelCount(2)
                ->setDeviceId(mDeviceId)
                ->openStream(mStream);
    }

    void setPerformance(oboe::PerformanceMode mode) {
        mStreamBuilder.setPerformanceMode(mode)->openStream(mStream);
    }

    void restart() {
        start();
    }

    void stop() {
        std::lock_guard<std::mutex> lock(mLock);
        if(mStream) {
            mStream->stop();
        }
    }

    oboe::Result reopenStream() {
        {
            // Stop and close in case not already closed.
            std::lock_guard<std::mutex> lock(mLock);
            if (mStream) {
                mStream->stop();
                mStream->close();
            }
        }
        return start();
    }

    oboe::Result start() {
        std::lock_guard<std::mutex> lock(mLock);

        auto result = createPlaybackStream();
        if(result == oboe::Result::OK) {
            mAudioSource = std::make_shared<EpnySoundGenerator>(mStream->getSampleRate(), mStream->getChannelCount());
            mCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(mAudioSource));
            mStream->start();
            mIsLatencyDetectionSupported = (mStream->getTimestamp((CLOCK_MONOTONIC)) != oboe::Result::ErrorUnimplemented);
            mStatus = RUNNING;
            LOGD("EUPHONY / EpnyTxEngine: %s", oboe::convertToText(result));
        } else {
            mStatus = STOP;
            LOGE("Error creating playback stream. Error: %s", oboe::convertToText(result));
        }

        return result;
    }

    void setBufferSizeInBursts(int32_t numBursts)
    {
        std::lock_guard<std::mutex> lock(mLock);
        if (!mStream) return;

        mIsLatencyDetectionSupported = false;
        mCallback->setBufferTuneEnabled(numBursts == kBufferSizeAutomatic);
        auto result = mStream->setBufferSizeInFrames(
                numBursts * mStream->getFramesPerBurst());
        if (result) {
            LOGD("Buffer size successfully changed to %d", result.value());
        } else {
            LOGW("Buffer size could not be changed, %d", result.error());
        }
    }

    double getCurrentOutputLatencyMillis() {
        if (!mIsLatencyDetectionSupported) return -1.0;

        std::lock_guard<std::mutex> lock(mLock);
        if (!mStream) return -1.0;

        // Get the time that a known audio frame was presented for playing
        auto result = mStream->getTimestamp(CLOCK_MONOTONIC);
        double outputLatencyMillis = -1;
        const int64_t kNanosPerMillisecond = 1000000;
        if (result == oboe::Result::OK) {
            oboe::FrameTimestamp playedFrame = result.value();
            // Get the write index for the next audio frame
            int64_t writeIndex = mStream->getFramesWritten();
            // Calculate the number of frames between our known frame and the write index
            int64_t frameIndexDelta = writeIndex - playedFrame.position;
            // Calculate the time which the next frame will be presented
            int64_t frameTimeDelta = (frameIndexDelta * oboe::kNanosPerSecond) /  (mStream->getSampleRate());
            int64_t nextFramePresentationTime = playedFrame.timestamp + frameTimeDelta;
            // Assume that the next frame will be written at the current time
            using namespace std::chrono;
            int64_t nextFrameWriteTime =
                    duration_cast<nanoseconds>(steady_clock::now().time_since_epoch()).count();
            // Calculate the latency
            outputLatencyMillis = static_cast<double>(nextFramePresentationTime - nextFrameWriteTime)
                                  / kNanosPerMillisecond;
        } else {
            LOGE("Error calculating latency: %s", oboe::convertToText(result.error()));
        }

        return outputLatencyMillis;
    }

    bool isLatencyDetectionSupported() {
        return mIsLatencyDetectionSupported;
    }
};

EpnyTxEngine::EpnyTxEngine()
: pImpl(std::make_unique<Impl>())
{ }

EpnyTxEngine::~EpnyTxEngine() = default;

void EpnyTxEngine::tap(bool isDown) {
    pImpl->mAudioSource->tap(isDown);
}

void EpnyTxEngine::stop() {
    pImpl->stop();
}

void EpnyTxEngine::start() {
    pImpl->start();
}

bool EpnyTxEngine::isLatencyDetectionSupported() {
    return pImpl->isLatencyDetectionSupported();
}

void EpnyTxEngine::setAudioApi(oboe::AudioApi audioApi) {
    pImpl->mAudioApi = audioApi;
}

void EpnyTxEngine::setPerformance(oboe::PerformanceMode mode) {
    pImpl->setPerformance(mode);
}

void EpnyTxEngine::setChannelCount(int channelCount) {
    pImpl->mChannelCount = channelCount;
}

void EpnyTxEngine::setDeviceId(int32_t deviceId) {
    pImpl->mDeviceId = deviceId;
}

void EpnyTxEngine::setBufferSizeInBursts(int32_t numBursts) {
    pImpl->setBufferSizeInBursts(numBursts);
}

double EpnyTxEngine::getCurrentOutputLatencyMillis() {
    return pImpl->getCurrentOutputLatencyMillis();
}

EpnyStatus EpnyTxEngine::getStatus() {
    return pImpl->mStatus;
}