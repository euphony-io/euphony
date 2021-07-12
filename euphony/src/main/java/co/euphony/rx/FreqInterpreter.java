package co.euphony.rx;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import co.euphony.util.EuOption;

public class FreqInterpreter {
    private static final String TAG = "FREQ_INTERPRETER";
    private EuOption rxOption;
    protected Map<Integer, Integer> freq2IdxMap = new HashMap<>();
    private Map<Integer, Integer> idx2FreqMap = new HashMap<>();

    private int startIndex = 0;
    private int lastIndex = 0;

    public FreqInterpreter() {
        this(new EuOption());
    }

    public FreqInterpreter(EuOption option) {
        rxOption = option;
        init();
        startIndex = getIdxByFreq(rxOption.getOutsetFrequency());
        lastIndex = startIndex + rxOption.getDataRate();

    }

    private void init() {
        for (int i = -1; i <= rxOption.getDataRate(); i++) {
            int freq = rxOption.getControlPoint() + rxOption.getDataInterval() * i;
            int freqIdx = ((int)((freq / 22050.0) * rxOption.getFFTSize() / 2));
            freq2IdxMap.put(freq, freqIdx);
            idx2FreqMap.put(freqIdx, freq);
        }
    }

    public int getStartFreq() { return rxOption.getOutsetFrequency(); }

    public int getStartIdx() { return startIndex; }

    public int getLastIdx() { return lastIndex; }

    public int getCodeIdxByFreq(int freq) {
        return freq2IdxMap.get(freq) - startIndex - 1;
    }

    public int getCodeIdxByIdx(int idx) {
        return idx - startIndex - 1;
    }

    public int getIdxByFreq(int freq) {
        return freq2IdxMap.get(freq);
    }

    public int getFreqByIdx(int idx) {
        return idx2FreqMap.get(idx);
    }
}