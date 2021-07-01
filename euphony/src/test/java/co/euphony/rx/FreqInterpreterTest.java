package co.euphony.rx;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FreqInterpreterTest {

    FreqInterpreter freqReader;

    int[] idxArray = {
            208, 209, 210, 211, 212, 213, 214, 215, 216,
            217, 218, 219, 220, 221, 222, 223, 224
    };

    int[] freqArray = {
            17914, 18000, 18086, 18172, 18258, 18344, 18430, 18516, 18602,
            18688, 18774, 18860, 18946, 19032, 19118, 19204, 19290
    };

    @Before
    public void setUp() throws Exception {
        freqReader = new FreqInterpreter();
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