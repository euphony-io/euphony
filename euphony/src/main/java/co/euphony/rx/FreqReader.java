package co.euphony.rx;

import java.util.HashMap;
import java.util.Map;

import co.euphony.util.EuOption;

public class FreqReader {

    private EuOption rxOption;
    protected Map<Integer, Integer> freq2IdxMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> idx2FreqMap = new HashMap<Integer, Integer>();

    public FreqReader() {
        rxOption = new EuOption();
        init();
    }

    public FreqReader(EuOption option) {
        rxOption = option;
        init();
    }

    private void init() {
        for (int i = 0; i < rxOption.getDataRate(); i++) {
            int freq = rxOption.getControlPoint() + rxOption.getDataInterval() * i;
            int freqIdx = ((int)((freq / 22050.0) * rxOption.getFFTSize() / 2)) + 1;
            freq2IdxMap.put(freq, freqIdx);
            idx2FreqMap.put(freqIdx, freq);
        }
    }

    public int getIndexByFreq(int freq) {
        return freq2IdxMap.get(freq);
    }

    public int getFreqByIndex(int idx) {
        return idx2FreqMap.get(idx);
    }
}