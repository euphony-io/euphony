package co.euphony.rx;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.euphony.util.EuOption;

public class CodeDecoder {
    private final String TAG = "CodeDecoder";

    EuOption option;
    ArrayList<Integer> codeIdxArray = new ArrayList<>();
    HashMap<Integer, Integer> codeCountMap = new HashMap<>();
    private int oneCodeSize = 0;
    private int outsetFreq;

    public CodeDecoder() {
        this(new EuOption());
    }

    public CodeDecoder(EuOption option) {
        this.option = option;
        init();
    }

    private void init() {
        oneCodeSize = (option.getBufferSize() / (option.getFFTSize() >> 1));
        outsetFreq = option.getOutsetFrequency();
    }

    public void addCodeIdx(int codeIdx) {
        codeIdxArray.add(codeIdx);
    }

    public String getGenCode() {
        StringBuilder code = new StringBuilder();

        int oneCodeCheck = 0;
        int sumCodeIdx = 0;

        for(int codeIdx : codeIdxArray) {
            oneCodeCheck++;
            countCodeIdx(codeIdx);
            if(oneCodeCheck == oneCodeSize) {
                int bestCodeIdx = getBestCodeIdx();
                Log.d(TAG,  "sumCodeIdx(" + sumCodeIdx + "), " + bestCodeIdx);

                oneCodeCheck = 0;
                sumCodeIdx = 0;
                codeCountMap.clear();

                if(isStartPoint(bestCodeIdx)) {
                    code.append("S");
                    continue;
                }

                if(isMoreThan0xA(bestCodeIdx))
                    code.append("").append((char) ('a' + (bestCodeIdx - 10)));
                else
                    code.append("").append(bestCodeIdx);

            }
        }

        return code.toString();
    }

    private void countCodeIdx(int codeIdx) {
        Integer codeCount = codeCountMap.get(codeIdx);
        if (codeCount == null) {
            codeCountMap.put(codeIdx, 1);
        } else {
            codeCountMap.put(codeIdx, codeCount + 1);
        }
    }

    private int getBestCodeIdx() {
        int bestCodeIdx = 0;
        int maxCodeIdxCount = 0;
        for(Map.Entry<Integer, Integer> e : codeCountMap.entrySet()) {
            if(e.getValue() > maxCodeIdxCount) {
                bestCodeIdx = e.getKey();
                maxCodeIdxCount = e.getValue();
            }
        }
        return bestCodeIdx;
    }


    private boolean isStartPoint(int codeIdx) { return codeIdx == -1; }
    private boolean isMoreThan0xA(int hex) { return hex > 9; }
}
