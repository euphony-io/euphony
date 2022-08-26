package co.euphony.common;

import android.content.Context;
import android.util.Log;

import co.euphony.util.EuOption;

public class EuNativeConnector {
    long mTxEngineHandle = 0;
    long mRxEngineHandle = 0;

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
        if(!createTxEngine())
            Log.e("EUPHONY_ERROR","Euphony Tx Engine Creation was failed.");
        else
            Log.d("EUPHONY_MSG","Euphon Tx Engine Creation was successful");

        if(!createRxEngine())
            Log.e("EUPHONY_ERROR", "Euphony Rx Engine Creation was failed.");
        else
            Log.d("EUPHONY_MSG", "Euphony Rx Engine creation was successful");

    }


    boolean createTxEngine() {
        if(mTxEngineHandle == 0)
            mTxEngineHandle = native_createTxEngine();

        return (mTxEngineHandle != 0);
    }

    boolean createRxEngine() {
        if(mRxEngineHandle == 0)
            mRxEngineHandle = native_createRxEngine();

        return (mTxEngineHandle != 0);
    }

    void clean() {
        if(mTxEngineHandle != 0)
            native_deleteTxEngine(mTxEngineHandle);

        if(mRxEngineHandle != 0)
            native_deleteRxEngine(mRxEngineHandle);

        mTxEngineHandle = 0;
        mRxEngineHandle = 0;
    }

    public Constants.Result tx_start() {
        if(mTxEngineHandle != 0) {
            final int res = native_tx_start(mTxEngineHandle);
            return Constants.Result.fromInteger(res);
        }
        else
            return Constants.Result.ERROR_GENERAL;
    }

    public void tx_stop() {
        if(mTxEngineHandle != 0)
            native_tx_stop(mTxEngineHandle);
    }

    public Constants.Result rx_start() {
        if(mRxEngineHandle != 0) {
            final int res = native_rx_start(mRxEngineHandle);
            return Constants.Result.fromInteger(res);
        }
        else
            return Constants.Result.ERROR_GENERAL;
    }

    public void rx_stop() {
        if(mRxEngineHandle != 0)
            native_rx_stop(mRxEngineHandle);
    }

    public void setPerformance(EpnyPerformanceMode mode, Context context) {
        if(mTxEngineHandle != 0) {
            switch(mode) {
                case PowerSavingMode:
                    native_setPerformance(mTxEngineHandle, 0);
                    break;
                case NormalMode:
                    native_setPerformance(mTxEngineHandle, 1);
                    break;
                case SuperPowerMode:
                    native_setPerformance(mTxEngineHandle, 2);
                    break;
            }
        }
    }

    public void setCodingType(EuOption.CodingType codingType) {
        if(mTxEngineHandle != 0) {
            native_setCodingType(mTxEngineHandle, codingType.ordinal());
        }
    }

    public void setMode(EuOption.ModeType modeType) {
        if(mTxEngineHandle != 0) {
            native_setMode(mTxEngineHandle, modeType.ordinal());
        }
    }

    public void setModulation(EuOption.ModulationType modulationType) {
        if(mTxEngineHandle != 0) {
            native_setModulation(mTxEngineHandle, modulationType.ordinal());
        }
    }

    public void setToneOn(boolean isToneOn) {
        if(mTxEngineHandle != 0) native_setToneOn(mTxEngineHandle, isToneOn);
    }

    public void setCountToneOn(boolean isToneOn, int count) {
        if(mTxEngineHandle != 0) native_setCountToneOn(mTxEngineHandle, isToneOn, count);
    }

    public void setCode(String data) {
        if(mTxEngineHandle != 0) native_setCode(mTxEngineHandle, data);
    }

    public String getCode() {
        if(mTxEngineHandle != 0)
            return native_getCode(mTxEngineHandle);
        return null;
    }

    public String getGenCode() {
        if(mTxEngineHandle != 0)
            return native_getGenCode(mTxEngineHandle);
        return null;
    }

    public float[] getGenWaveSource() {
        if(mTxEngineHandle != 0)
            return native_getGenWaveSource(mTxEngineHandle);
        return null;
    }

    public void setAudioFrequency(double freq) {
        if(mTxEngineHandle != 0) native_setAudioFrequency(mTxEngineHandle, freq);
    }

    public void setAudioApi(int audioApi){
        if (mTxEngineHandle != 0) native_setAudioApi(mTxEngineHandle, audioApi);
    }

    public void setAudioDeviceId(int deviceId){
        if (mTxEngineHandle != 0) native_setAudioDeviceId(mTxEngineHandle, deviceId);
    }

    public int getFramesPerBursts(){
        if (mTxEngineHandle != 0) return native_getFramesPerBursts(mTxEngineHandle);
        else return -1;
    }

    public void setBufferSizeInBursts(int bufferSizeInBursts){
        if (mTxEngineHandle != 0) native_setBufferSizeInBursts(mTxEngineHandle, bufferSizeInBursts);
    }

    public double getCurrentOutputLatencyMillis(){
        if (mTxEngineHandle == 0) return 0;
        return native_getCurrentOutputLatencyMillis(mTxEngineHandle);
    }

    public boolean isLatencyDetectionSupported() {
        return mTxEngineHandle != 0 && native_isLatencyDetectionSupported(mTxEngineHandle);
    }

    public EpnyStatus getStatus() {
        if(mTxEngineHandle == 0) return EpnyStatus.NO_CREATE;
        switch(native_getStatus(mTxEngineHandle)) {
            case 0:
                return EpnyStatus.RUNNING;
            case 1:
                return EpnyStatus.STOP;
        }
        return EpnyStatus.NO_CREATE;
    }

    private native long native_createTxEngine();
    private native void native_deleteTxEngine(long engineHandle);
    private native long native_createRxEngine();
    private native void native_deleteRxEngine(long engineHandle);
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
