package co.euphony.rx;

import org.junit.Before;
import org.junit.Test;

import co.euphony.util.EuOption;

import static org.junit.Assert.*;

public class FreqInterpreterTest {

    FreqInterpreter freqReader;

    int[] idxArray = {
            208, 209, 210, 211, 212, 213, 214, 215, 216,
            217, 218, 219, 220, 221, 222, 223, 224
    };

    int[] freqArray = {
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    @Before
    public void setUp() throws Exception {
        freqReader = new FreqInterpreter();
        EuOption option = new EuOption();
        for(int i = 0; i < 17; i++) {
            freqArray[i] = option.getOutsetFrequency() + (option.getDataInterval() * i);
        }
    }

    @Test
    public void getIndexByFreq() {
        for(int i = 0; i < 17; i++) {
            int actual = freqReader.getIdxByFreq(freqArray[i]);
            assertEquals(idxArray[i], actual);
        }
    }

    @Test
    public void getFreqByIndex() {
        for(int i = 0 ; i < 17; i++) {
            int actual = freqReader.getFreqByIdx(idxArray[i]);
            assertEquals(freqArray[i], actual);
        }
    }

    @Test
    public void getCodeIdxByFreq() {
        for(int i = 0 ; i < 17; i++) {
            int actual = freqReader.getCodeIdxByFreq(freqArray[i]);
            assertEquals(i - 1, actual);
        }
    }

    @Test
    public void getCodeIdxByIdx() {
        for(int i = 0 ; i < 17; i++) {
            int actual = freqReader.getCodeIdxByIdx(idxArray[i]);
            assertEquals(i - 1, actual);
        }
    }

}