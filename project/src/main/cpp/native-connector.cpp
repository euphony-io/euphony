//
// Created by opener on 20. 8. 25.
//

#include <jni.h>
#include <oboe/Oboe.h>
#include <Log.h>
#include "EpnyTxEngine.h"

extern "C" {
    JNIEXPORT jlong JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1createEngine(JNIEnv *env, jclass clazz) {
        EpnyTxEngine * engine = new(std::nothrow) EpnyTxEngine();
        return reinterpret_cast<jlong>(engine);
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1deleteEngine(JNIEnv *env, jclass clazz,
                                                                jlong engine_handle) {
        delete reinterpret_cast<EpnyTxEngine *>(engine_handle);
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setToneOn(JNIEnv *env, jclass clazz,
                                                             jlong engine_handle,
                                                             jboolean is_tone_on) {
        EpnyTxEngine *engine = reinterpret_cast<EpnyTxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        engine->tap(is_tone_on);
    }

    /*
    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setAudioApi(JNIEnv *env, jclass clazz,
                                                               jlong engine_handle,
                                                               jint audio_api) {
        EpnyTxEngine *engine = reinterpret_cast<EpnyTxEngine *> (engine_handle);
        if(engine == nullptr) {
            LOGE("Engine handle is invalid, call createHandle() to create a new one");
            return;
        }

        // TODO: implement native_setAudioApi()
    }*/

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setAudioDeviceId(JNIEnv *env, jclass clazz,
                                                                    jlong engine_handle,
                                                                    jint device_id) {
        // TODO: implement native_setAudioDeviceId()
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setChannelCount(JNIEnv *env, jclass clazz,
                                                                   jlong m_engine_handle,
                                                                   jint channel_count) {
        // TODO: implement native_setChannelCount()
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setBufferSizeInBursts(JNIEnv *env, jclass clazz,
                                                                         jlong engine_handle,
                                                                         jint buffer_size_in_bursts) {
        // TODO: implement native_setBufferSizeInBursts()
    }

    /*
    JNIEXPORT jdouble JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1getCurrentOutputLatencyMillis(JNIEnv *env,
                                                                                 jclass clazz,
                                                                                 jlong engine_handle) {
        // TODO: implement native_getCurrentOutputLatencyMillis()
    }

    JNIEXPORT jboolean JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1isLatencyDetectionSupported(JNIEnv *env,
                                                                               jclass clazz,
                                                                               jlong engine_handle) {
        // TODO: implement native_isLatencyDetectionSupported()
    }*/

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setDefaultStreamValues(JNIEnv *env, jclass clazz,
                                                                          jint sample_rate,
                                                                          jint frames_per_burst) {
        // TODO: implement native_setDefaultStreamValues()
    }
}