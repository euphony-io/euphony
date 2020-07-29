//
// Created by desig on 2020-07-22.
//

#ifndef EUPHONY_EUPHONYTXCORE_H
#define EUPHONY_EUPHONYTXCORE_H

#include <oboe/AudioStreamCallback.h>
#include <memory>

class EuphonyTxCore : public oboe::AudioStreamCallback {
public:
    ~EuphonyTxCore() override = default;

    oboe::DataCallbackResult
    onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

    void onErrorBeforeClose(oboe::AudioStream *stream, oboe::Result result) override;

    void onErrorAfterClose(oboe::AudioStream *stream, oboe::Result result) override;

private:
    class impl; std::unique_ptr<impl> pimpl;
};


#endif //EUPHONY_EUPHONYTXCORE_H
