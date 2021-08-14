package co.euphony.tx;

import android.util.Log;

import androidx.test.filters.SmallTest;
import androidx.test.filters.Suppress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.FloatBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;

import co.euphony.rx.FFTStrategy;
import co.euphony.rx.FreqInterpreter;
import co.euphony.rx.KissFFTWrapper;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@SmallTest
public class CodeMakerTest {
    private static final String TX_TAG = "CODE_MAKER_TAG";
    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"S6284", 5*2048, 5*8},
                {"S6375", 5*2048, 5*8},
                {"S61626386", 9*2048, 9*8},
                {"S6c6d6e6f20", 11*2048, 11*8},
                {"S656667c2", 9*2048, 9*8},
                {"S6162636465666768696a6b6c6d6e6f707172737475767778797aaa", 55*2048, 55*8},
                {"S414243e4", 9*2048, 9*8},
                {"S4142434445464748494a4b4c4d4e4f505152535455565758595aea", 55*2048, 55*8},
        });
    }

    FFTStrategy fft;
    FreqInterpreter freqInterpreter = new FreqInterpreter();

    String expectedGenCode;
    int expectedStreamLength;
    int expectedBufferLength;
    public CodeMakerTest(String expectedGenCode, int expectedStreamLength, int expectedBufferLength) {
        fft = null;
        this.expectedGenCode = expectedGenCode;
        this.expectedStreamLength = expectedStreamLength;
        this.expectedBufferLength = expectedBufferLength;
    }

    private int getMaxIndexOfFrequencies(float[] array) {
        int maxIndex = 0;
        float maxValue = 0;
        for(int j = freqInterpreter.getStartIdx(); j <= freqInterpreter.getLastIdx(); j++) {
            if(array[j] > maxValue) {
                maxValue = array[j];
                maxIndex = j;
            }
        }
        Log.d(TX_TAG, " / " + maxIndex + " / " + maxValue + " / " + freqInterpreter.getFreqByIdx(maxIndex) + " / " + freqInterpreter.getCodeIdxByIdx(maxIndex));
        return maxIndex;
    }

    private char translateIdx2ASCII(int idx) {
        if(idx == -1)
            return 'S';
        if(idx < 10)
            return (char)(idx + '0');
        else
            return (char)((idx - 10) + 'a');
    }

    @Test
    @Suppress
    public void code_maker_test() {
        /*
        *
        EuCodeMaker codeMaker = new EuCodeMaker();

        short[] stream = codeMaker.assembleData(expectedGenCode);
        assertEquals(expectedStreamLength, stream.length);

        fft = new KissFFTWrapper(512);
        FloatBuffer[] buf = fft.makeSpectrum(stream);
        assertEquals(expectedBufferLength, buf.length);
        fft.finish();

        int codeLength = (expectedBufferLength >> 3);
        for(int i = 0; i < codeLength; i++) {
            float[] floatArray1, floatArray2;
            try {
                floatArray1 = buf[(i * 8) + 3].array();
                floatArray2 = buf[(i * 8) + 4].array();

                int maxIndex1 = getMaxIndexOfFrequencies(floatArray1);
                int maxIndex2 = getMaxIndexOfFrequencies(floatArray2);

                assertEquals(expectedGenCode.charAt(i), translateIdx2ASCII(freqInterpreter.getCodeIdxByIdx(maxIndex1)));
                assertEquals(expectedGenCode.charAt(i), translateIdx2ASCII(freqInterpreter.getCodeIdxByIdx(maxIndex2)));
            } catch (ReadOnlyBufferException rbe) {
                rbe.printStackTrace();
            } catch (UnsupportedOperationException uoe) {
                uoe.printStackTrace();
            }
        }
         */
    }
}
