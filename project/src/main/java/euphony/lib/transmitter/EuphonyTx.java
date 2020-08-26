package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;


public class EuphonyTx {
    long mEngineHandle = 0;

    static {
        System.loadLibrary("euphony");
    }

    EuphonyTx(Context context) {
        create(context);
    }

    EuphonyTx(int sampleRate, int framesPerBurst) {
        create(sampleRate, framesPerBurst);
    }

    boolean create(int sampleRate, int framesPerBurst) {
        if(mEngineHandle == 0) {
            setDefaultStreamValues(sampleRate, framesPerBurst);
            mEngineHandle = native_createEngine();
        }

        return (mEngineHandle != 0);
    }

    boolean create(Context context) {
        if(mEngineHandle == 0) {
            setDefaultStreamValues(context);
            mEngineHandle = native_createEngine();
        }

        return (mEngineHandle != 0);
    }

    void clean() {
        if(mEngineHandle != 0)
            native_deleteEngine(mEngineHandle);

        mEngineHandle = 0;
    }

    private void setDefaultStreamValues(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            if(defaultSampleRate == 0) defaultSampleRate = 44100;

            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);
            if(defaultFramesPerBurst == 0) defaultFramesPerBurst = 256; // Use Default
            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
        }
    }
    private void setDefaultStreamValues(int sampleRate, int framesPerBurst) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            native_setDefaultStreamValues(sampleRate, framesPerBurst);
        }
    }

    void setToneOn(boolean isToneOn) {
        if(mEngineHandle != 0) native_setToneOn(mEngineHandle, isToneOn);
    }


    void setAudioApi(int audioApi){
        if (mEngineHandle != 0) native_setAudioApi(mEngineHandle, audioApi);
    }

    void setAudioDeviceId(int deviceId){
        if (mEngineHandle != 0) native_setAudioDeviceId(mEngineHandle, deviceId);
    }

    void setChannelCount(int channelCount) {
        if (mEngineHandle != 0) native_setChannelCount(mEngineHandle, channelCount);
    }

    void setBufferSizeInBursts(int bufferSizeInBursts){
        if (mEngineHandle != 0) native_setBufferSizeInBursts(mEngineHandle, bufferSizeInBursts);
    }

    double getCurrentOutputLatencyMillis(){
        if (mEngineHandle == 0) return 0;
        return native_getCurrentOutputLatencyMillis(mEngineHandle);
    }

    boolean isLatencyDetectionSupported() {
        return mEngineHandle != 0 && native_isLatencyDetectionSupported(mEngineHandle);
    }

    private native long native_createEngine();
    private native void native_deleteEngine(long engineHandle);
    private native void native_setToneOn(long engineHandle, boolean isToneOn);
    private native void native_setAudioApi(long engineHandle, int audioApi);
    private native void native_setAudioDeviceId(long engineHandle, int deviceId);
    private native void native_setChannelCount(long engineHandle, int channelCount);
    private native void native_setBufferSizeInBursts(long engineHandle, int bufferSizeInBursts);
    private native double native_getCurrentOutputLatencyMillis(long engineHandle);
    private native boolean native_isLatencyDetectionSupported(long engineHandle);
    private native void native_setDefaultStreamValues(int sampleRate, int framesPerBurst);
}
