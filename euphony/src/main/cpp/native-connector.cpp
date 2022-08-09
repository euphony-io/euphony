//
// Created by opener on 20. 8. 25.
//

#include <jni.h>
#include <android/api-level.h>
#include <oboe/Oboe.h>
#include "debug-helper/Log.h"
#include "core/TxEngine.h"
#include "core/RxEngine.h"

using namespace Euphony;

extern "C" {

    int getSampleRateForDevice(JNIEnv *env) {
        jclass audioSystem = env->FindClass("android/media/AudioSystem");
        jmethodID method = env->GetStaticMethodID(audioSystem, "getPrimaryOutputSamplingRate", "()I");
        jint outputSampleRate = env->CallStaticIntMethod(audioSystem, method);
        LOGD("This device's samplerate for output : %d", outputSampleRate);

        if(outputSampleRate == 0)
            return 44100;
        else
            return outputSampleRate;
    }

    int getFramesPerBurstForDevice(JNIEnv *env) {
        jclass audioSystem = env->FindClass("android/media/AudioSystem");
        jmethodID method = env->GetStaticMethodID(audioSystem, "getPrimaryOutputFrameCount", "()I");
        jint bufferLength = env->CallStaticIntMethod(audioSystem, method);
        LOGD("This device's frames per buffer for output : %d", bufferLength);

        if(bufferLength == 0)
            return 256;
        else
            return bufferLength;
    }

    JNIEXPORT jlong JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1createTxEngine(JNIEnv *env, jobject thiz) {
        auto engine = new(std::nothrow) TxEngine();

        /* if Android Version >= JELLEY_BEAN_MR1_LEVEL(17) */
        if(android_get_device_api_level() >= 17) {
            /* Setting for default SampleRate & FramesPerBurst */
            oboe::DefaultStreamValues::SampleRate = (int32_t) getSampleRateForDevice(env);
            oboe::DefaultStreamValues::FramesPerBurst = (int32_t) getFramesPerBurstForDevice(env);
        }

        return reinterpret_cast<jlong>(engine);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1deleteTxEngine(JNIEnv *env, jobject thiz,
                                                                    jlong engine_handle) {
        delete reinterpret_cast<TxEngine *>(engine_handle);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1deleteRxEngine(JNIEnv *env, jobject thiz,
                                                                jlong engine_handle) {
        delete reinterpret_cast<RxEngine *>(engine_handle);
    }

    JNIEXPORT jlong JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1createRxEngine(JNIEnv *env, jobject thiz) {
        auto engine = new(std::nothrow) RxEngine();

        return reinterpret_cast<jlong>(engine);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setToneOn(JNIEnv *env, jobject thiz,
                                                             jlong engine_handle,
                                                             jboolean is_tone_on) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->tap(is_tone_on);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setCountToneOn(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                                  jboolean is_tone_on, jint count) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->tapCount(is_tone_on, count);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setCode(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                           jstring data) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        const char* inputData = env->GetStringUTFChars(data, 0);
        const std::string inputStr = std::string(inputData);

        engine->setCode(inputStr);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setCodingType(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                                 jint type) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setCodingType(type);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setMode(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                           jint mode) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setMode(mode);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setModulation(JNIEnv *env, jobject thiz, jlong engine_handle,
                                                                 jint modulation_type) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setModulation(modulation_type);
    }

    JNIEXPORT jstring JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getCode(JNIEnv *env, jobject thiz, jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return nullptr;
        }

        return env->NewStringUTF(engine->getCode().c_str());
    }

    JNIEXPORT jstring JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getGenCode(JNIEnv *env, jobject thiz, jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return nullptr;
        }

        return env->NewStringUTF(engine->getGenCode().c_str());
    }

    JNIEXPORT jfloatArray JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getGenWaveSource(JNIEnv *env, jobject thiz,
                                                                    jlong engine_handle) {
        jfloatArray result;

        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return nullptr;
        }

        float* genWaveSource = engine->getGenWaveSource();
        int genWaveSourceSize = engine->getGenWaveSourceSize();

        result = env->NewFloatArray(genWaveSourceSize);
        env->SetFloatArrayRegion(result, 0, genWaveSourceSize, genWaveSource);

        return result;
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1tx_1start(JNIEnv *env, jobject thiz,
                                                               jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return static_cast<jint>(Result::ERROR_GENERAL);
        }

        return (jint) engine->start();
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1tx_1stop(JNIEnv *env, jobject thiz,
                                                              jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->stop();
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1rx_1start(JNIEnv *env, jobject thiz,
                                                               jlong engine_handle) {
        auto engine = reinterpret_cast<RxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Rx Engine is invalid, call createHandle() to create a new one");
            return static_cast<jint>(Result::ERROR_GENERAL);
        }

        return (jint) engine->start();
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1rx_1stop(JNIEnv *env, jobject thiz,
                                                              jlong engine_handle) {
        auto engine = reinterpret_cast<RxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Rx Engine is invalid, call createHandle() to create a new one");
            return;
        }

        engine->stop();
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getStatus(JNIEnv *env, jobject thiz,
                                                             jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return static_cast<jint>(Status::STOP);
        }

        return static_cast<jint>(engine->getStatus());
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setAudioApi(JNIEnv *env, jobject thiz,
                                                               jlong engine_handle,
                                                               jint audio_api) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        auto api = static_cast<oboe::AudioApi>(audio_api);
        engine->setAudioApi(api);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setAudioDeviceId(JNIEnv *env, jobject thiz,
                                                                    jlong engine_handle,
                                                                    jint device_id) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setDeviceId(device_id);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setAudioFrequency(JNIEnv *env, jobject thiz,
                                                                     jlong engine_handle,
                                                                     jdouble frequency) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setEupiFrequency(frequency);
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setBufferSizeInBursts(JNIEnv *env, jobject thiz,
                                                                         jlong engine_handle,
                                                                         jint buffer_size_in_bursts) {

        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->setBufferSizeInBursts(buffer_size_in_bursts);
    }


    JNIEXPORT jdouble JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getCurrentOutputLatencyMillis(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return static_cast<jdouble>(-1.0);
        }

        return static_cast<jdouble>(engine->getCurrentOutputLatencyMillis());
    }

    JNIEXPORT jboolean JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1isLatencyDetectionSupported(JNIEnv *env,
                                                                               jobject thiz,
                                                                               jlong engine_handle) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return JNI_FALSE;
        }

        return (engine->isLatencyDetectionSupported()) ? JNI_TRUE : JNI_FALSE;
    }

    JNIEXPORT void JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1setPerformance(JNIEnv *env, jobject thiz,
                                                                  jlong engine_handle,
                                                                  jint performance_level) {
        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
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
            default:
                engine->setPerformance(oboe::PerformanceMode::LowLatency);
                break;
        }
    }

    JNIEXPORT jint JNICALL
    Java_co_euphony_common_EuNativeConnector_native_1getFramesPerBursts(JNIEnv *env, jobject thiz,
                                                                      jlong engine_handle) {

        auto engine = reinterpret_cast<TxEngine *> (engine_handle);
        if (engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return -1;
        }

        return engine->getFramesPerBursts();

    }
}
