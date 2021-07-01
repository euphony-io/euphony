package co.euphony.rx;

import android.util.Log;

import java.util.ArrayList;

import co.euphony.util.EuOption;

public class CodeDecoder {
    private final String TAG = "CodeDecoder";

    EuOption option;
    ArrayList<Integer> codeIdxArray = new ArrayList<>();
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
        int codeIdxCount = 0;
        int sumCodeIdx = 0;
        for(int codeIdx : codeIdxArray) {
            sumCodeIdx += codeIdx;
            codeIdxCount++;
            if(codeIdxCount == oneCodeSize) {
                int avgCodeIdx = sumCodeIdx >> 3;
                Log.d(TAG,  "sumCodeIdx(" + sumCodeIdx + "), " + avgCodeIdx);

                codeIdxCount = 0;
                sumCodeIdx = 0;

                if(isStartPoint(avgCodeIdx)) {
                    code.append("S");
                    continue;
                }

                if(isMoreThan0xA(avgCodeIdx))
                    code.append("").append((char) ('a' + (avgCodeIdx - 10)));
                else
                    code.append("").append(avgCodeIdx);

            }
        }

        return code.toString();
    }

    private boolean isStartPoint(int codeIdx) { return codeIdx == -1; }
    private boolean isMoreThan0xA(int hex) { return hex > 9; }
}
