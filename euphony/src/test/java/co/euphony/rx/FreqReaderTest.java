package co.euphony.rx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FreqReaderTest {

    FreqReader freqReader;

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
        freqReader = new FreqReader();
    }

    @Test
    public void getIndexByFreq() {
        for(int i = 0; i < 17; i++) {
            int expected = freqReader.getIndexByFreq(freqArray[i]);
            assertEquals(expected, idxArray[i]);
        }
    }

    @Test
    public void getFreqByIndex() {
        for(int i = 0 ; i < 17; i++) {
            int expected = freqReader.getFreqByIndex(idxArray[i]);
            assertEquals(expected, freqArray[i]);
        }
    }
}