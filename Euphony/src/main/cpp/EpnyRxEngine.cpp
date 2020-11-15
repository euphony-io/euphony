//
// Created by desig on 2020-11-15.
//

#include <oboe/Oboe.h>
#include <Log.h>
#include "EpnyRxEngine.h"

class EpnyRxEngine::EpnyRxEngineImpl {
public:
    std::mutex mLock;
    bool isRecording;
    std::shared_ptr<oboe::AudioStream> mStream;
    oboe::AudioStreamBuilder mStreamBuilder;

    void start() {
        std::lock_guard<std::mutex> lock(mLock);

        mStreamBuilder.setDirection(oboe::Direction::Input);
        mStreamBuilder.setPerformanceMode(oboe::PerformanceMode::LowLatency);

        oboe::Result res = mStreamBuilder.openStream(mStream);

        if( res != oboe::Result::OK ) {
            LOGE("Error opening stream: %s", convertToText(res));
        }

        res = mStream->requestStart();
        if( res != oboe::Result::OK ) {
            LOGE("Error starting stream : %s", convertToText(res));
        }

        constexpr int kMillisecondsToRecord = 2;
        const int32_t requestedFrames = (int32_t) (kMillisecondsToRecord * (mStream->getSampleRate() / oboe::kMillisPerSecond));
        int16_t myBuffer[requestedFrames];

        constexpr int64_t kTimeoutValue = 3 * oboe::kNanosPerMillisecond;

        while(isRecording) {
            auto result = mStream->read(myBuffer, requestedFrames, kTimeoutValue);

            if(result == oboe::Result::OK) {
                LOGD("Read %d frames", result.value());
            } else {
                LOGE("Error reading stream: %s", convertToText(result.error()));
            }
        }

        mStream->close();
    }

    void stop() {
        std::lock_guard<std::mutex> lock(mLock);
        if(mStream) {
            mStream->stop();
        }
    }
};

void EpnyRxEngine::start() {
    pImpl->start();
}

void EpnyRxEngine::stop() {
    pImpl->stop();
}