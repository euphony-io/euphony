package euphony.lib.transmitter;

import org.junit.Test;
import euphony.lib.transmitter.EuDataEncoder;
import euphony.lib.util.EuOption;

import static org.junit.Assert.assertEquals;
public class TransmitterUnitTest {
    @Test
    public void encode_hex_isCorrect()
    {
        EuDataEncoder mEuDataEncoder = new EuDataEncoder("hell");
        assertEquals(mEuDataEncoder.getOriginalSource(), "hell");
        assertEquals(mEuDataEncoder.encodeHexCharSource(), "68656c6c");
        assertEquals(EuDataEncoder.encodeStaticHexCharSource("hello, euphony"),
                "68656c6c6f2c20657570686f6e79");
    }

    @Test
    public void EuTxManager_iscorrect() {
        EuTxManager mEuTxManager = new EuTxManager();
        mEuTxManager.setCode("Hello, Euphony");
        int length = 63488;
        int outStreamLength = mEuTxManager.getOutStream().length;
        assertEquals(outStreamLength, length);

        EuTxManager mEuTxManager2 = new EuTxManager(new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.LIVE));
        mEuTxManager2.setCode("Hello, Euphony");
        length = 231424;
        outStreamLength = mEuTxManager2.getOutStream().length;
        assertEquals(outStreamLength, length);
    }
}
