//
// Created by designe on 20. 8. 24.
//

#ifndef EUPHONY_AUDIOSTREAMCALLBACK_H
#define EUPHONY_AUDIOSTREAMCALLBACK_H

#include <oboe/Oboe.h>
#include <oboe/LatencyTuner.h>

#include <TappableAudioSource.h>
#include <DefaultAudioStreamCallback.h>
#include <Trace.h>

/**
 * This callback object extends the functionality of `DefaultAudioStreamCallback` by automatically
 * tuning the latency of the audio stream. @see onAudioReady for more details on this.
 *
 * It also demonstrates how to use tracing functions for logging inside the audio callback without
 * blocking.
 */

namespace Euphony {
    class AudioStreamCallback : public DefaultAudioStreamCallback {
    public:
        AudioStreamCallback(IRestartable &mParent) : DefaultAudioStreamCallback(mParent) {

            // Initialize the trace functions, this enables you to output trace statements without
            // blocking. See https://developer.android.com/studio/profile/systrace-commandline.html
            Trace::initialize();
        }

        /**
         * Every time the playback stream requires data this method will be called.
         *
         * @param audioStream the audio stream which is requesting data, this is the mPlayStream object
         * @param audioData an empty buffer into which we can write our audio data
         * @param numFrames the number of audio frames which are required
         * @return Either oboe::DataCallbackResult::Continue if the stream should continue requesting data
         * or oboe::DataCallbackResult::Stop if the stream should stop.
         */
        oboe::DataCallbackResult
        onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) override;

        void setBufferTuneEnabled(bool enabled) { mBufferTuneEnabled = enabled; }

        void useStream(std::shared_ptr<oboe::AudioStream> stream);

    private:
        bool mBufferTuneEnabled = true;

        // This will be used to automatically tune the buffer size of the stream, obtaining optimal latency
        std::unique_ptr<oboe::LatencyTuner> mLatencyTuner;
        oboe::AudioStream *mStream = nullptr;
    };
}

#endif //EUPHONY_AUDIOSTREAMCALLBACK_H
