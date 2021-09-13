package co.euphony.rx;

public class EuPI {
    public enum EuPITrigger {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    public enum EuPIStatus {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    private int mKey;
    private int mFreqIndex;
    EuPITrigger mTrigger;
    EuPIStatus mStatus;
    EuPICallDetector mAPICallback;

    public EuPI(int key, EuPICallDetector callback) {
        mKey = key;
        mAPICallback = callback;
        mTrigger = EuPITrigger.KEY_PRESSED;
        mStatus = EuPIStatus.KEY_UP;
    }

    public EuPI(int key, EuPITrigger trigger, EuPICallDetector callback) {
        mKey = key;
        mAPICallback = callback;
        mTrigger = trigger;
        mStatus = EuPIStatus.KEY_UP;
    }

    public int getKey() {
        return mKey;
    }

    public void setFreqIndex(int idx) {
        mFreqIndex = idx;
    }

    public void setTrigger(EuPITrigger trigger) {
        mTrigger = trigger;
    }

    public EuPITrigger getTrigger() {
        return mTrigger;
    }

    public void setStatus(EuPIStatus status) {
        mStatus = status;
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

}
