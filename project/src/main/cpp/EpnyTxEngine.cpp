//
// Created by desig on 2020-08-15.
//
#include <oboe/Oboe.h>
#include <Log.h>
#include "EpnyTxEngine.h"
#include "EpnySoundGenerator.h"
#include "EpnyAudioStreamCallback.h"

class EpnyTxEngine::impl : public IRestartable{
public:
    std::mutex mLock;
    oboe::ManagedStream mStream;
    std::unique_ptr<EpnyAudioStreamCallback> mCallback;
    std::shared_ptr<EpnySoundGenerator> mAudioSource;

    impl() {
        createCallback();
        start();
    }

    virtual ~impl() = default;

    void createCallback() {
        mCallback = std::make_unique<EpnyAudioStreamCallback>(*this);
    }

    oboe::Result createPlaybackStream() {
        oboe::AudioStreamBuilder builder;

        return builder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(mCallback.get())
                ->openManagedStream(mStream);

    }

    void restart() {
        start();
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
        if(!mStream) return oboe::Result::ErrorNull;

        auto result = createPlaybackStream();
        if(result == oboe::Result::OK) {
            mAudioSource = std::make_shared<EpnySoundGenerator>(mStream->getSampleRate(), mStream->getChannelCount());
            mCallback->setSource(std::dynamic_pointer_cast<IRenderableAudio>(mAudioSource));
            mStream->start();
        } else {
            LOGE("Error creating playback stream. Error: %s", oboe::convertToText(result));
        }
    }
};