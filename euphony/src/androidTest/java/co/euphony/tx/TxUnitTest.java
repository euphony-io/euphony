package co.euphony.tx;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.FloatBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import co.euphony.rx.FFTStrategy;
import co.euphony.rx.FreqReader;
import co.euphony.rx.KissFFTWrapper;
import co.euphony.util.EuOption;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TxUnitTest {
    static final String TX_TAG = "TX_TAG";
    private float[] expectedResult;
    private int streamLength = 0;
    FFTStrategy fft;
    FreqReader freqReader = new FreqReader();
    @Before
    public void setup() {
        fft = new KissFFTWrapper(512);
    }

    @Test
    public void tx_default_test() {
        EuTxManager txManager = new EuTxManager();
        txManager.setCode("a");
        streamLength = txManager.getOutStream().length;
        assertEquals(streamLength, 10240);

        FloatBuffer[] buf = fft.makeSpectrum(txManager.getOutStream());
        assertEquals(buf.length, 41);
        fft.finish();

        for(int i = 0; i < buf.length; i++) {
            float[] floatArray = new float[(fft.getFFTSize() >> 1) + 1];
            try {
                floatArray = buf[i].array();
                //buf[i].get(floatArray);
                int maxIndex = 0;
                float maxValue = 0;
                for(int j = 0; j < 224; j++) {
                    if(floatArray[j] > maxValue) {
                        maxValue = floatArray[j];
                        maxIndex = j;
                    }
                }
                //Log.d(TX_TAG, i + " / " + maxIndex + " / " + maxValue + " / " + freqReader.getFreqByIndex(maxIndex));
            } catch (ReadOnlyBufferException rbe) {
                rbe.printStackTrace();
            } catch (UnsupportedOperationException uoe) {
                uoe.printStackTrace();
            }
        }

/*        txManager.setCode("Hello, Euphony");
        streamLength = txManager.getOutStream().length;
        assertEquals(streamLength, 63488);
*/
        //Assert.assertArrayEquals(buf.array(), expectedResult);
        //buf.array();

        //txManager.process();
        //txManager.process(3);
    }

    @Test
    public void tx_ascii_live_fsk_test() {
        EuTxManager mEuTxManager2 = new EuTxManager(new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.LIVE, EuOption.ModulationType.FSK));
        mEuTxManager2.setCode("Hello, Euphony");
        streamLength = mEuTxManager2.getOutStream().length;
        assertEquals(streamLength, 253952);
    }
}
