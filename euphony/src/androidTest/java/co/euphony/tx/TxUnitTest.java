package co.euphony.tx;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.FloatBuffer;
import java.nio.ReadOnlyBufferException;

import co.euphony.rx.CodeDecoder;
import co.euphony.rx.FFTStrategy;
import co.euphony.rx.FreqInterpreter;
import co.euphony.rx.KissFFTWrapper;
import co.euphony.util.EuOption;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TxUnitTest {
    private static final String TX_TAG = "TX_TAG";
    private static final String a_CODE = "a";
    /* S (beginCode) + 61 (ASCII a) + 97 (ErrorCode) */
    private static final String a_GEN_CODE = "S6197";
    /* 2048(buffer length) * 5(code length) = 10240 */
    private static final int a_GEN_CODE_LENGTH = 10240;


    private float[] expectedResult;
    private int streamLength = 0;
    FFTStrategy fft;
    FreqInterpreter freqInterpreter = new FreqInterpreter();
    CodeDecoder codeDecoder = new CodeDecoder();
    @Before
    public void setup() {
        fft = new KissFFTWrapper(512);
    }

    @Test
    public void tx_default_test() {
        EuTxManager txManager = new EuTxManager();
        txManager.setCode(a_CODE);
        Assert.assertThat(txManager.getGenCode(), is(a_GEN_CODE));

        streamLength = txManager.getOutStream().length;
        assertEquals(streamLength, a_GEN_CODE_LENGTH);

        FloatBuffer[] buf = fft.makeSpectrum(txManager.getOutStream());
        assertEquals(buf.length, 40);
        fft.finish();

        for(int i = 0; i < buf.length; i++) {
            float[] floatArray;
            try {
                floatArray = buf[i].array();
                int maxIndex = 0;
                float maxValue = 0;
                for(int j = freqInterpreter.getStartIdx(); j < freqInterpreter.getLastIdx(); j++) {
                    if(floatArray[j] > maxValue) {
                        maxValue = floatArray[j];
                        maxIndex = j;
                    }
                }
                Log.d(TX_TAG, i + " / " + maxIndex + " / " + maxValue + " / " + freqInterpreter.getFreqByIdx(maxIndex));
                codeDecoder.addCodeIdx(freqInterpreter.getCodeIdxByIdx(maxIndex));
            } catch (ReadOnlyBufferException rbe) {
                rbe.printStackTrace();
            } catch (UnsupportedOperationException uoe) {
                uoe.printStackTrace();
            }
        }
        Log.d(TX_TAG, codeDecoder.getGenCode());
        assertEquals(a_GEN_CODE, codeDecoder.getGenCode());
    }

    @Test
    public void tx_ascii_live_fsk_test() {
        EuTxManager mEuTxManager2 = new EuTxManager(new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.LIVE, EuOption.ModulationType.FSK));
        mEuTxManager2.setCode("Hello, Euphony");
        streamLength = mEuTxManager2.getOutStream().length;
        assertEquals(streamLength, 253952);
    }
}
