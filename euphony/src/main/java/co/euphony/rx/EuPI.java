package co.euphony.rx;

import co.euphony.common.Constants;

public class EuPI {
    public enum EuPITrigger {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    public enum EuPIStatus {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    private int mKey;
    private int mFreqIndex;
    private double mThreshold;
    private double mInputThreshold;
    EuPITrigger mTrigger;
    EuPIStatus mStatus;
    EuPICallDetector mAPICallback;

    public EuPI(int key, EuPICallDetector callback) {
        mKey = key;
        mFreqIndex = calculateFreqIndex(key);
        mAPICallback = callback;
        mTrigger = EuPITrigger.KEY_PRESSED;
        mInputThreshold = 0;
        setStatus(EuPIStatus.KEY_UP);
    }

    public EuPI(int key, EuPITrigger trigger, EuPICallDetector callback) {
        mKey = key;
        mFreqIndex = calculateFreqIndex(key);
        mAPICallback = callback;
        mTrigger = trigger;
        mInputThreshold = 0;
        setStatus(EuPIStatus.KEY_UP);
    }

    public EuPI(int key, double threshold, EuPITrigger trigger, EuPICallDetector callback) {
        mKey = key;
        mAPICallback = callback;
        mTrigger = trigger;
        mInputThreshold = threshold;
        setStatus(EuPIStatus.KEY_UP);
    }

    public int getKey() {
        return mKey;
    }

    public boolean compareThreshold(float amp) { return amp >= mThreshold; }

    public void setTrigger(EuPITrigger trigger) {
        mTrigger = trigger;
    }

    public EuPITrigger getTrigger() {
        return mTrigger;
    }

    public void setStatus(EuPIStatus status) {
        mStatus = status;
        switch(status) {
            case KEY_UP:
            default:
                mThreshold = (mInputThreshold == 0) ? 0.0009 : mInputThreshold;
                break;
            case KEY_DOWN:
                mThreshold = (mInputThreshold == 0) ? 0.0005 : mInputThreshold - 0.0004;
                break;
        }
    }

    public EuPIStatus getStatus() {
        return mStatus;
    }

    public int getFreqIndex() {
        return mFreqIndex;
    }

    public EuPICallDetector getCallback() {
        return mAPICallback;
    }

    private int calculateFreqIndex(int freq) {
        double freqRatio = ((float)freq) / (float) Constants.HALF_SAMPLERATE;
        return (int) Math.round((freqRatio * (float) (Constants.FFT_SIZE >> 1)));
    }
}
