//
// Created by opener on 20. 8. 25.
//

#include <jni.h>
#include <oboe/Oboe.h>

extern "C" {
    JNIEXPORT jlong JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1createEngine(JNIEnv *env, jclass clazz) {
        // TODO: implement native_createEngine()
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1deleteEngine(JNIEnv *env, jclass clazz,
                                                                jlong engine_handle) {
        // TODO: implement native_deleteEngine()
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setToneOn(JNIEnv *env, jclass clazz,
                                                             jlong engine_handle,
                                                             jboolean is_tone_on) {
        // TODO: implement native_setToneOn()
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setAudioApi(JNIEnv *env, jclass clazz,
                                                               jlong engine_handle,
                                                               jint audio_api) {
        // TODO: implement native_setAudioApi()
    }

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
    }

    JNIEXPORT void JNICALL
    Java_euphony_lib_transmitter_EuphonyTx_native_1setDefaultStreamValues(JNIEnv *env, jclass clazz,
                                                                          jint sample_rate,
                                                                          jint frames_per_burst) {
        // TODO: implement native_setDefaultStreamValues()
    }
}