//
// Created by designe on 20. 8. 24.
//

#include <AudioStreamCallback.h>

oboe::DataCallbackResult Euphony::AudioStreamCallback::onAudioReady(
        oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {
    if (oboeStream != mStream) {
        mStream = oboeStream;
        mLatencyTuner = std::make_unique<oboe::LatencyTuner>(*oboeStream);
    }
    if (mBufferTuneEnabled
        && mLatencyTuner
        && oboeStream->getAudioApi() == oboe::AudioApi::AAudio) {
        mLatencyTuner->tune();
    }
    auto underrunCountResult = oboeStream->getXRunCount();
    int bufferSize = oboeStream->getBufferSizeInFrames();
    /**
     * The following output can be seen by running a systrace. Tracing is preferable to logging
    * inside the callback since tracing does not block.
    *
    * See https://developer.android.com/studio/profile/systrace-commandline.html
    */
    if (Trace::isEnabled()) Trace::beginSection("numFrames %d, Underruns %d, buffer size %d",
                                                numFrames, underrunCountResult.value(), bufferSize);
    auto result = DefaultAudioStreamCallback::onAudioReady(oboeStream, audioData, numFrames);
    if (Trace::isEnabled()) Trace::endSection();
    return result;
}