package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;


public class EuphonyTx {
    static long mEngineHandle = 0;

    static {
        System.loadLibrary("euphony");
    }

    static boolean create(int sampleRate, int framesPerBurst) {
        if(mEngineHandle == 0) {
            setDefaultStreamValues(sampleRate, framesPerBurst);
            mEngineHandle = native_createEngine();
        }

        return (mEngineHandle != 0);
    }

    static boolean create(Context context) {
        if(mEngineHandle == 0) {
            setDefaultStreamValues(context);
            mEngineHandle = native_createEngine();
        }

        return (mEngineHandle != 0);
    }

    static void delete() {
        if(mEngineHandle != 0)
            native_deleteEngine(mEngineHandle);

        mEngineHandle = 0;
    }

    private static void setDefaultStreamValues(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AudioManager myAudioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            int defaultSampleRate = Integer.parseInt(sampleRateStr);
            String framesPerBurstStr = myAudioMgr.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            int defaultFramesPerBurst = Integer.parseInt(framesPerBurstStr);

            native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst);
        }
    }
    private static void setDefaultStreamValues(int sampleRate, int framesPerBurst) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            native_setDefaultStreamValues(sampleRate, framesPerBurst);
        }
    }

    static void setToneOn(boolean isToneOn) {
        if(mEngineHandle != 0) native_setToneOn(mEngineHandle, isToneOn);
    }

    /*
    static void setAudioApi(int audioApi){
        if (mEngineHandle != 0) native_setAudioApi(mEngineHandle, audioApi);
    }
*/
    static void setAudioDeviceId(int deviceId){
        if (mEngineHandle != 0) native_setAudioDeviceId(mEngineHandle, deviceId);
    }

    static void setChannelCount(int channelCount) {
        if (mEngineHandle != 0) native_setChannelCount(mEngineHandle, channelCount);
    }

    static void setBufferSizeInBursts(int bufferSizeInBursts){
        if (mEngineHandle != 0) native_setBufferSizeInBursts(mEngineHandle, bufferSizeInBursts);
    }
/*
    static double getCurrentOutputLatencyMillis(){
        if (mEngineHandle == 0) return 0;
        return native_getCurrentOutputLatencyMillis(mEngineHandle);
    }

    static boolean isLatencyDetectionSupported() {
        return mEngineHandle != 0 && native_isLatencyDetectionSupported(mEngineHandle);
    }
*/
    private static native long native_createEngine();
    private static native void native_deleteEngine(long engineHandle);
    private static native void native_setToneOn(long engineHandle, boolean isToneOn);
    //private static native void native_setAudioApi(long engineHandle, int audioApi);
    private static native void native_setAudioDeviceId(long engineHandle, int deviceId);
    private static native void native_setChannelCount(long mEngineHandle, int channelCount);
    private static native void native_setBufferSizeInBursts(long engineHandle, int bufferSizeInBursts);
    //private static native double native_getCurrentOutputLatencyMillis(long engineHandle);
    //private static native boolean native_isLatencyDetectionSupported(long engineHandle);
    private static native void native_setDefaultStreamValues(int sampleRate, int framesPerBurst);
}
