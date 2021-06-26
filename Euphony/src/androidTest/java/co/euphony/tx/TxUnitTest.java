package co.euphony.tx;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.FloatBuffer;

import co.euphony.rx.FFTStrategy;
import co.euphony.rx.KissFFTWrapper;
import co.euphony.util.EuOption;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TxUnitTest {

    private float[] expectedResult;
    private int streamLength = 0;
    FFTStrategy fft;

    @Before
    public void setup() {
        fft = new KissFFTWrapper(1024);
    }

    @Test
    public void tx_default_test() {
        EuTxManager txManager = new EuTxManager();
        txManager.setCode("A");
        streamLength = txManager.getOutStream().length;
        assertEquals(streamLength, 10240);

        txManager.setCode("Hello, Euphony");
        streamLength = txManager.getOutStream().length;
        assertEquals(streamLength, 63488);

        //FloatBuffer buf = fft.makeSpectrum(txManager.getOutStream());

        //Assert.assertArrayEquals(buf.array(), expectedResult);
        //buf.array();

        txManager.process();
        txManager.process(3);
    }

    @Test
    public void tx_ascii_live_fsk_test() {
        EuTxManager mEuTxManager2 = new EuTxManager(new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.LIVE, EuOption.ModulationType.FSK));
        mEuTxManager2.setCode("Hello, Euphony");
        streamLength = mEuTxManager2.getOutStream().length;
        assertEquals(streamLength, 253952);
    }
}
