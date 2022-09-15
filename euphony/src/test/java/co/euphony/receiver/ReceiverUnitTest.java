package co.euphony.receiver;

import org.junit.Test;

import co.euphony.rx.EuDataDecoder;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ReceiverUnitTest {

    // EuDataDecoder
    @Test
    public void decode_hex_isCorrect() {
        EuDataDecoder mEuDataDecoder = new EuDataDecoder("68656c6c");
        assertEquals(mEuDataDecoder.decodeHexCharSource(), "hell");
        assertEquals(EuDataDecoder.decodeStaticHexCharSource("68656c6c6f2c20657570686f6e79"),
                "hello, euphony");
        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
        assertEquals(EuDataDecoder.decodeStaticHexCharSource(source), "hello");
    }
}