package euphony.lib.transmitter;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;


public class EuphonyTx {
    long mEngineHandle = 0;

    public enum EpnyStatus {
        RUNNING, STOP, NO_CREATE
    }

    public enum EpnyMethod {
        APIMode,
        MessageMode
    }

    public enum EpnyPerformanceMode {
        PowerSavingMode,
        NormalMode,
        SuperPowerMode
    }

    static {
        System.loadLibrary("euphony");
    }

    public EuphonyTx() {
        if(create() != true) {
            Log.d("EUPHONY_ERROR","Euphony Engine Creation was failed.");
        } else {
            Log.d("EUPHONY_MSG","Euphony Engine Creation was successful");
        }
    }

    public static EuphonyTx newInstance() {
        return new EuphonyTx();
    }

    boolean create() {
        if(mEngineHandle == 0)
            mEngineHandle = native_createEngine();

        return (mEngineHandle != 0);
    }

    void clean() {
        if(mEngineHandle != 0)
            native_deleteEngine(mEngineHandle);

        mEngineHandle = 0;
    }

    public void start() {
        if(mEngineHandle != 0)
            native_start(mEngineHandle);
    }

    public void stop() {
        if(mEngineHandle != 0)
            native_stop(mEngineHandle);
    }

    public void setPerformance(EpnyPerformanceMode mode, Context context) {
        if(mEngineHandle != 0) {
            switch(mode) {
                case PowerSavingMode:
                    native_setPerformance(mEngineHandle, 0);
                    break;
                case NormalMode:
                    native_setPerformance(mEngineHandle, 1);
                    break;
                case SuperPowerMode:
                    native_setPerformance(mEngineHandle, 2);
                    break;
            }
        }
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

    public void setToneOn(boolean isToneOn) {
        if(mEngineHandle != 0) native_setToneOn(mEngineHandle, isToneOn);
    }

    public void setAudioApi(int audioApi){
        if (mEngineHandle != 0) native_setAudioApi(mEngineHandle, audioApi);
    }

    public void setAudioDeviceId(int deviceId){
        if (mEngineHandle != 0) native_setAudioDeviceId(mEngineHandle, deviceId);
    }

    public void setChannelCount(int channelCount) {
        if (mEngineHandle != 0) native_setChannelCount(mEngineHandle, channelCount);
    }

    public void setBufferSizeInBursts(int bufferSizeInBursts){
        if (mEngineHandle != 0) native_setBufferSizeInBursts(mEngineHandle, bufferSizeInBursts);
    }

    public double getCurrentOutputLatencyMillis(){
        if (mEngineHandle == 0) return 0;
        return native_getCurrentOutputLatencyMillis(mEngineHandle);
    }

    public boolean isLatencyDetectionSupported() {
        return mEngineHandle != 0 && native_isLatencyDetectionSupported(mEngineHandle);
    }

    public EpnyStatus getStatus() {
        if(mEngineHandle == 0) return EpnyStatus.NO_CREATE;
        switch(native_getStatus(mEngineHandle)) {
            case 0:
                return EpnyStatus.RUNNING;
            case 1:
                return EpnyStatus.STOP;
        }
        return EpnyStatus.NO_CREATE;
    }

    private native long native_createEngine();
    private native void native_deleteEngine(long engineHandle);
    private native void native_start(long engineHandle);
    private native void native_stop(long engineHandle);
    private native int native_getStatus(long engineHandle);
    private native void native_setPerformance(long engineHandle, int performanceLevel);
    private native void native_setToneOn(long engineHandle, boolean isToneOn);
    private native void native_setAudioApi(long engineHandle, int audioApi);
    private native void native_setAudioDeviceId(long engineHandle, int deviceId);
    private native void native_setChannelCount(long engineHandle, int channelCount);
    private native void native_setBufferSizeInBursts(long engineHandle, int bufferSizeInBursts);
    private native double native_getCurrentOutputLatencyMillis(long engineHandle);
    private native boolean native_isLatencyDetectionSupported(long engineHandle);
    private native void native_setDefaultStreamValues(int sampleRate, int framesPerBurst);
}
