#include "../RxEngine.h"
#include "../FFTProcessor.h"
#include <oboe/Oboe.h>
#include <Log.h>

using namespace Euphony;

class RxEngine::RxEngineImpl : oboe::AudioStreamCallback {
public:
    oboe::AudioStreamBuilder mStreamBuilder;
    std::shared_ptr<oboe::AudioStream> mStream;
    oboe::Result mStreamResult = oboe::Result::ErrorBase;

    int32_t mDeviceId = oboe::Unspecified;

    bool isRecording = false;

    RxEngineImpl() {
        /* 1) Create Input Stream Callback */
        /* 2) Create Input Stream */
        mStreamResult = createRxStream();
        if(mStreamResult == oboe::Result::OK)
            LOGD("EUPHONY / EpnyRxEngine: %s", oboe::convertToText(mStreamResult));
        else
            LOGD("Error creating RX stream. Error: %s", oboe::convertToText(mStreamResult));
    }

    oboe::DataCallbackResult onAudioReady(
            oboe::AudioStream *stream, void *inputData, int32_t numFrames
            ) override {
        oboe::DataCallbackResult result = oboe::DataCallbackResult::Continue;

        /* Definitions for input processing */
        const float *inputFloats = static_cast<const float *>(inputData);

        int32_t samplesPerFrame = stream->getChannelCount();
        int32_t numInputSamples = numFrames * samplesPerFrame;

        /* Input Data Processing Part */

        /* End */

        return result;
    }

    oboe::Result createRxStream() {
        return mStreamBuilder.setDeviceId(mDeviceId)
        ->setDirection(oboe::Direction::Input)
        ->setSampleRate(kSampleRate)
        ->setChannelCount(kChannelCount)
        ->setSharingMode(oboe::SharingMode::Exclusive)
        ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
        ->setFormat(oboe::AudioFormat::Float)
        ->openStream(mStream);
    }

    Euphony::Result start() {
        isRecording = true;
        mStream->requestStart();
        return Euphony::Result::OK;
    }

    void stop() {
        mStream->requestStop();
        isRecording = false;
    }
};

RxEngine::RxEngine()
: pImpl(std::make_unique<RxEngineImpl>()) { }

RxEngine::~RxEngine() = default;

Euphony::Result RxEngine::start() {
    return pImpl->start();
}

void RxEngine::stop() {
    pImpl->stop();
}

