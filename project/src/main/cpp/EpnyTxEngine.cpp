//
// Created by desig on 2020-08-15.
//
#include <oboe/Oboe.h>
#include "EpnyTxEngine.h"
#include "EpnySoundGenerator.h"
#include "Log.h"
#include <DefaultAudioStreamCallback.h>

class EpnyTxEngine::impl : public IRestartable{
public:

    oboe::ManagedStream mStream;
    std::unique_ptr<DefaultAudioStreamCallback> mCallback;
    std::shared_ptr<EpnySoundGenerator> mAudioSource;
    void createCallback(std::vector<int> cpuIds) {
        mCallback = std::make_unique<DefaultAudioStreamCallback>(*this);
    }

    oboe::Result createPlaybackStream() {
        oboe::AudioStreamBuilder builder;

        return builder.setSharingMode(oboe::SharingMode::Exclusive)
                ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
                ->setFormat(oboe::AudioFormat::Float)
                ->setCallback(mCallback.get())
                ->openManagedStream(mStream);

    }

    void restart() override {

    }

    void start() {
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