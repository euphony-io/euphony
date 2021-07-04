package co.euphony.rx;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import co.euphony.common.Packet;
import co.euphony.util.EuOption;
import co.euphony.util.PacketErrorDetector;

/* TODO: CodeDecoder's renaming necessary : like a PacketDecoder (intuitive) */
public class CodeDecoder {
    private final String TAG = "CodeDecoder";

    EuOption option;
    ArrayList<Integer> codeIdxArray = new ArrayList<>();
    HashMap<Integer, Integer> codeCountMap = new HashMap<>();

    Packet packet;
    private int oneCodeSize = 0;
    //private int outsetFreq;

    public CodeDecoder() {
        this(new EuOption());
    }

    public CodeDecoder(EuOption option) {
        this.option = option;
        init();
    }

    private void init() {
        oneCodeSize = (option.getBufferSize() / (option.getFFTSize() >> 1));
        packet = new Packet();
        // outsetFreq = option.getOutsetFrequency();
    }

    public void addCodeIdx(int codeIdx) {
        codeIdxArray.add(codeIdx);
    }

    public Packet decodePacket() {
        if(packet.isVerified())
            return packet;

        int oneCodeCheck = 0;
        for(int codeIdx : codeIdxArray) {
            oneCodeCheck++;

            if (oneCodeCheck == 1) {
                continue;
            }
            else if (oneCodeCheck == oneCodeSize) {
                makeCode(getBestCodeIdx());
                oneCodeCheck = 0;
                codeCountMap.clear();
            } else {
                countCodeIdx(codeIdx);
            }
        }

        return packet;
    }

    private void makeCode(final int bestCodeIdx) {
        Log.d(TAG,  "bestCodeIdx = " + bestCodeIdx);

        if(isStartPoint(bestCodeIdx)) {
            return;
        } else {
            packet.push(bestCodeIdx);
        }
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
        if(codeCountMap.size() == 1)
            return (int) codeCountMap.keySet().toArray()[0];

        int bestCodeIdx = 0;
        int maxCodeIdxCount = 0;

        for(Map.Entry<Integer, Integer> e : codeCountMap.entrySet()) {
            if(e.getValue() > maxCodeIdxCount) {
                bestCodeIdx = e.getKey();
                maxCodeIdxCount = e.getValue();
            }
        }
        Log.d(TAG,  bestCodeIdx + " of " + codeCountMap.size() + " on getBestCodeIdx()");
        return bestCodeIdx;
    }

    private boolean isStartPoint(int codeIdx) { return codeIdx == -1; }
}
