package euphony.rx;

public class EpnyAPI {
    public enum EpnyAPITrigger {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    public enum EpnyAPIStatus {
        KEY_DOWN, KEY_UP, KEY_PRESSED
    }

    private int mKey;
    private int mFreqIndex;
    EpnyAPITrigger mTrigger;
    EpnyAPIStatus mStatus;
    APICallDetector mAPICallback;

    public EpnyAPI(int key, APICallDetector callback) {
        mKey = key;
        mAPICallback = callback;
        mTrigger = EpnyAPITrigger.KEY_PRESSED;
        mStatus = EpnyAPIStatus.KEY_UP;
    }

    public EpnyAPI(int key, EpnyAPITrigger trigger, APICallDetector callback) {
        mKey = key;
        mAPICallback = callback;
        mTrigger = trigger;
        mStatus = EpnyAPIStatus.KEY_UP;
    }

    public int getKey() {
        return mKey;
    }

    public void setFreqIndex(int idx) {
        mFreqIndex = idx;
    }

    public void setTrigger(EpnyAPITrigger trigger) {
        mTrigger = trigger;
    }

    public EpnyAPITrigger getTrigger() {
        return mTrigger;
    }

    public void setStatus(EpnyAPIStatus status) {
        mStatus = status;
    }

    public EpnyAPIStatus getStatus() {
        return mStatus;
    }

    public int getFreqIndex() {
        return mFreqIndex;
    }

    public APICallDetector getCallback() {
        return mAPICallback;
    }

}
