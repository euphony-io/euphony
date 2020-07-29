//
// Created by desig on 2020-07-22.
//

#include "EuphonyTxCore.h"
#include "Log.h"
#include <vector>
#include <cstdint>
#include <unistd.h>
#include <sched.h>

class EuphonyTxCore::impl {
public:
    std::vector<int> mCpuIds;
    std::atomic<bool> mIsThreadAffinityEnabled { false };
    std::atomic<bool> mIsThreadAffinitySet { false };

    void setThreadAffinity() {
        pid_t current_thread_id = gettid();
        cpu_set_t cpu_set;
        CPU_ZERO(&cpu_set);

        // If the callback cpu ids aren't specified then bind to the current cpu
        if (mCpuIds.empty()) {
            int current_cpu_id = sched_getcpu();
            LOGD("Binding to current CPU ID %d", current_cpu_id);
            CPU_SET(current_cpu_id, &cpu_set);
        } else {
            LOGD("Binding to %d CPU IDs", static_cast<int>(mCpuIds.size()));
            for (size_t i = 0; i < mCpuIds.size(); i++) {
                int cpu_id = mCpuIds.at(i);
                LOGD("CPU ID %d added to cores set", cpu_id);
                CPU_SET(cpu_id, &cpu_set);
            }
        }

        int result = sched_setaffinity(current_thread_id, sizeof(cpu_set_t), &cpu_set);
        if (result == 0) {
            LOGV("Thread affinity set");
        } else {
            LOGW("Error setting thread affinity. Error no: %d", result);
        }

        mIsThreadAffinitySet = true;
    }
};

EuphonyTxCore::~EuphonyTxCore() {

}

oboe::DataCallbackResult
EuphonyTxCore::onAudioReady(oboe::AudioStream *oboeStream, void *audioData, int32_t numFrames) {

    return oboe::DataCallbackResult::Continue;
}

void EuphonyTxCore::onErrorBeforeClose(oboe::AudioStream *stream, oboe::Result result) {
    AudioStreamCallback::onErrorBeforeClose(stream, result);
}

void EuphonyTxCore::onErrorAfterClose(oboe::AudioStream *stream, oboe::Result result) {
    AudioStreamCallback::onErrorAfterClose(stream, result);
}
