package co.euphony.common;

import android.content.Context;
import android.util.Log;

import co.euphony.util.EuOption;

public class EuNativeConnector {
    long mEngineHandle = 0;

    public enum EpnyStatus {
        RUNNING, STOP, NO_CREATE
    }

    public enum EpnyPerformanceMode {
        PowerSavingMode,
        NormalMode,
        SuperPowerMode
    }

    static {
        System.loadLibrary("euphony");
    }

    private static class InnerInstanceClazz {
        private static final EuNativeConnector instance = new EuNativeConnector();
    }

    public static EuNativeConnector getInstance() {
        return InnerInstanceClazz.instance;
    }

    private EuNativeConnector() {
        if(!create()){
            Log.e("EUPHONY_ERROR","Euphony Engine Creation was failed.");
        } else {
            Log.d("EUPHONY_MSG","Euphony Engine Creation was successful");
        }
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

    public Constants.Result tx_start() {
        if(mEngineHandle != 0) {
            final int res = native_tx_start(mEngineHandle);
            return Constants.Result.fromInteger(res);
        }
        else
            return Constants.Result.ERROR_GENERAL;
    }

    public void tx_stop() {
        if(mEngineHandle != 0)
            native_tx_stop(mEngineHandle);
    }

    public Constants.Result rx_start() {
        if(mEngineHandle != 0) {
            final int res = native_rx_start(mEngineHandle);
            return Constants.Result.fromInteger(res);
        }
        else
            return Constants.Result.ERROR_GENERAL;
    }

    public void rx_stop() {
        if(mEngineHandle != 0)
            native_rx_stop(mEngineHandle);
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

    public void setCodingType(EuOption.CodingType codingType) {
        if(mEngineHandle != 0) {
            native_setCodingType(mEngineHandle, codingType.ordinal());
        }
    }

    public void setMode(EuOption.ModeType modeType) {
        if(mEngineHandle != 0) {
            native_setMode(mEngineHandle, modeType.ordinal());
        }
    }

    public void setModulation(EuOption.ModulationType modulationType) {
        if(mEngineHandle != 0) {
            native_setModulation(mEngineHandle, modulationType.ordinal());
        }
    }

    public void setToneOn(boolean isToneOn) {
        if(mEngineHandle != 0) native_setToneOn(mEngineHandle, isToneOn);
    }

    public void setCountToneOn(boolean isToneOn, int count) {
        if(mEngineHandle != 0) native_setCountToneOn(mEngineHandle, isToneOn, count);
    }

    public void setCode(String data) {
        if(mEngineHandle != 0) native_setCode(mEngineHandle, data);
    }

    public String getCode() {
        if(mEngineHandle != 0)
            return native_getCode(mEngineHandle);
        return null;
    }

    public String getGenCode() {
        if(mEngineHandle != 0)
            return native_getGenCode(mEngineHandle);
        return null;
    }

    public float[] getGenWaveSource() {
        if(mEngineHandle != 0)
            return native_getGenWaveSource(mEngineHandle);
        return null;
    }

    public void setAudioFrequency(double freq) {
        if(mEngineHandle != 0) native_setAudioFrequency(mEngineHandle, freq);
    }

    public void setAudioApi(int audioApi){
        if (mEngineHandle != 0) native_setAudioApi(mEngineHandle, audioApi);
    }

    public void setAudioDeviceId(int deviceId){
        if (mEngineHandle != 0) native_setAudioDeviceId(mEngineHandle, deviceId);
    }

    public int getFramesPerBursts(){
        if (mEngineHandle != 0) return native_getFramesPerBursts(mEngineHandle);
        else return -1;
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
    private native int native_tx_start(long engineHandle);
    private native void native_tx_stop(long engineHandle);
    private native int native_rx_start(long engineHandle);
    private native void native_rx_stop(long engineHandle);
    private native int native_getStatus(long engineHandle);
    private native void native_setPerformance(long engineHandle, int performanceLevel);
    private native void native_setToneOn(long engineHandle, boolean isToneOn);
    private native void native_setCountToneOn(long engineHandle, boolean isToneOn, int count);
    private native void native_setCode(long engineHandle, String data);
    private native void native_setCodingType(long engineHandle, int type);
    private native void native_setMode(long engineHandle, int mode);
    private native void native_setModulation(long engineHandle, int modulationType);
    private native String native_getCode(long engineHandle);
    private native String native_getGenCode(long engineHandle);
    private native float[] native_getGenWaveSource(long engineHandle);
    private native void native_setAudioFrequency(long engineHandle, double frequency);
    private native void native_setAudioApi(long engineHandle, int audioApi);
    private native void native_setAudioDeviceId(long engineHandle, int deviceId);
    private native int native_getFramesPerBursts(long engineHandle);
    private native void native_setBufferSizeInBursts(long engineHandle, int bufferSizeInBursts);
    private native double native_getCurrentOutputLatencyMillis(long engineHandle);
    private native boolean native_isLatencyDetectionSupported(long engineHandle);
}
