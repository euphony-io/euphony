//
// Created by opener on 20. 8. 25.
//

#include <jni.h>
#include <oboe/Oboe.h>
#include "debug-helper/Log.h"
#include "core/TxEngine.h"

using namespace Euphony;

extern "C" {
    JNIEXPORT jlong JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1createEngine(JNIEnv *env, jobject thiz) {
        TxEngine * engine = new(std::nothrow) TxEngine();
        return reinterpret_cast<jlong>(engine);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1deleteEngine(JNIEnv *env, jobject thiz,
                                                                jlong engine_handle) {
        delete reinterpret_cast<TxEngine *>(engine_handle);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setToneOn(JNIEnv *env, jobject thiz,
                                                             jlong engine_handle,
                                                             jboolean is_tone_on) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->tap(is_tone_on);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setCountToneOn(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                        jboolean is_tone_on, jint count) {
        // TODO: implement native_setCountToneOn()
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setCode(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                 jstring data) {
        // TODO: implement native_setCode()
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1start(JNIEnv *env, jobject thiz,
                                                         jlong engine_handle) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->start();
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1stop(JNIEnv *env, jobject thiz,
                                                        jlong engine_handle) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->stop();
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1getStatus(JNIEnv *env, jobject thiz,
                                                             jlong engine_handle) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return 2;
        }

        switch(engine->getStatus()) {
            case RUNNING:
                return 0;
            case STOP:
                return 1;
        }

    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setAudioApi(JNIEnv *env, jobject thiz,
                                                               jlong engine_handle,
                                                               jint audio_api) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        oboe::AudioApi api = static_cast<oboe::AudioApi>(audio_api);
        engine->setAudioApi(api);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setAudioDeviceId(JNIEnv *env, jobject thiz,
                                                                    jlong engine_handle,
                                                                    jint device_id) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setDeviceId(device_id);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setAudioFrequency(JNIEnv *env, jobject thiz,
                                                                     jlong engine_handle,
                                                                     jdouble frequency) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setAudioFrequency(frequency);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setChannelCount(JNIEnv *env, jobject thiz,
                                                                   jlong engine_handle,
                                                                   jint channel_count) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setChannelCount(channel_count);

    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setBufferSizeInBursts(JNIEnv *env, jobject thiz,
                                                                         jlong engine_handle,
                                                                         jint buffer_size_in_bursts) {

        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setBufferSizeInBursts(buffer_size_in_bursts);
    }


    JNIEXPORT jdouble JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1getCurrentOutputLatencyMillis(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jlong engine_handle) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return static_cast<jdouble>(-1.0);
        }

        return static_cast<jdouble>(engine->getCurrentOutputLatencyMillis());
    }

    JNIEXPORT jboolean JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1isLatencyDetectionSupported(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong engine_handle) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return JNI_FALSE;
        }

        return (engine->isLatencyDetectionSupported()) ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setPerformance(JNIEnv *env, jobject thiz,
                                                                  jlong engine_handle,
                                                                  jint performance_level) {
        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        switch(performance_level) {
            case 0:
                engine->setPerformance(oboe::PerformanceMode::PowerSaving);
                break;
            case 1:
                engine->setPerformance(oboe::PerformanceMode::None);
                break;
            case 2:
                engine->setPerformance(oboe::PerformanceMode::LowLatency);
                break;
        }
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1setDefaultStreamValues(JNIEnv *env, jobject thiz,
                                                                          jint sample_rate,
                                                                          jint frames_per_burst) {
        oboe::DefaultStreamValues::SampleRate = (int32_t) sample_rate;
        oboe::DefaultStreamValues::FramesPerBurst = (int32_t) frames_per_burst;
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_tx_EuphonyTx_native_1getFramesPerBursts(JNIEnv *env, jobject thiz,
            jlong engine_handle) {

        TxEngine *engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return -1;
        }

        return engine->getFramesPerBursts();

    }
}