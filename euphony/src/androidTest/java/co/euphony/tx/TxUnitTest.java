package co.euphony.tx;

import android.util.Log;

import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.FloatBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;

import co.euphony.common.Packet;
import co.euphony.rx.CodeDecoder;
import co.euphony.rx.EuDataDecoder;
import co.euphony.rx.FFTStrategy;
import co.euphony.rx.FreqInterpreter;
import co.euphony.rx.KissFFTWrapper;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
@SmallTest
public class TxUnitTest {
    private static final String TX_TAG = "TX_TAG";
    private static final String a_CODE = "a";
    /* S (beginCode) + 61 (ASCII a) + 97 (ErrorCode) */
    private static final String a_GEN_CODE = "S6197";
    /* 2048(buffer length) * 5(code length) = 10240 */
    private static final int a_GEN_CODE_STREAM_LENGTH = 10240;
    /* 2048(buffer length) / 256 (fft-size / 2) * 5 (code length) = 40 */
    private static final int a_BUFFER_LENGTH = 40;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {a_CODE, a_GEN_CODE, a_GEN_CODE_STREAM_LENGTH, a_BUFFER_LENGTH},
                {"b", "S6284", 5*2048, 5*8},
                {"c", "S6375", 5*2048, 5*8},
                {"abc", "S61626386", 9*2048, 9*8},
                {"lmno", "S6c6d6e6f20", 11*2048, 11*8},
                {"efg", "S656667c2", 9*2048, 9*8},
                {"abcdefghijklmnopqrstuvwxyz", "S6162636465666768696a6b6c6d6e6f707172737475767778797aaa", 55*2048, 55*8},
                {"ABC", "S414243e4", 9*2048, 9*8},
                {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "S4142434445464748494a4b4c4d4e4f505152535455565758595aea", 55*2048, 55*8},
                });
    }

    private int streamLength = 0;
    FFTStrategy fft;
    FreqInterpreter freqInterpreter = new FreqInterpreter();
    CodeDecoder codeDecoder = new CodeDecoder();


    String code;
    String expectedGenCode;
    int expectedStreamLength;
    int expectedBufferLength;
    public TxUnitTest(String code, String expectedGenCode, int expectedStreamLength, int expectedBufferLength) {
        this.code = code;
        this.expectedGenCode = expectedGenCode;
        this.expectedStreamLength = expectedStreamLength;
        this.expectedBufferLength = expectedBufferLength;
    }

    @Before
    public void setup() {
        fft = null;
    }

    @Test
    /* Default Tx & Rx Test */
    public void tx_rx_default_test() {
        EuTxManager txManager = new EuTxManager();
        txManager.setCode(code);
        Assert.assertThat(txManager.getGenCode(), is(expectedGenCode));

        streamLength = txManager.getOutStream().length;
        assertEquals(expectedStreamLength, streamLength);

        fft = new KissFFTWrapper(512);
        FloatBuffer[] buf = fft.makeSpectrum(txManager.getOutStream());
        assertEquals(expectedBufferLength, buf.length);
        fft.finish();

        for(int i = 0; i < buf.length; i++) {
            float[] floatArray;
            try {
                floatArray = buf[i].array();
                int maxIndex = 0;
                float maxValue = 0;
                for(int j = freqInterpreter.getStartIdx(); j <= freqInterpreter.getLastIdx(); j++) {
                    if(floatArray[j] > maxValue) {
                        maxValue = floatArray[j];
                        maxIndex = j;
                    }
                }
                Log.d(TX_TAG, i + " / " + maxIndex + " / " + maxValue + " / " + freqInterpreter.getFreqByIdx(maxIndex) + " / " + freqInterpreter.getCodeIdxByIdx(maxIndex));
                codeDecoder.addCodeIdx(freqInterpreter.getCodeIdxByIdx(maxIndex));
            } catch (ReadOnlyBufferException rbe) {
                rbe.printStackTrace();
            } catch (UnsupportedOperationException uoe) {
                uoe.printStackTrace();
            }
        }
        Packet packet = codeDecoder.decodePacket();
        if(packet.build()) {
            Log.d(TX_TAG, "packet build success : " + packet.toString());
        } else {
            Log.d(TX_TAG, "packet build failed");
        }

        assertEquals(expectedGenCode, packet.toString());

        String result = EuDataDecoder.decodeStaticHexCharSource(packet.getPayload());
        assertEquals(code, result);
    }
}
