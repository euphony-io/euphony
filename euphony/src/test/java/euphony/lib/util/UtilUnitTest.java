package euphony.lib.util;

import org.junit.Test;
import euphony.lib.transmitter.EuDataEncoder;

import static org.junit.Assert.assertEquals;
public class UtilUnitTest {
    @Test
    public void packet_err_detect_isCorrect()
    {
        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
        assertEquals(PacketErrorDetector.makeCheckSum(source), 14);
        assertEquals(PacketErrorDetector.makeCheckSum(   234), 6);
        assertEquals(PacketErrorDetector.makeParellelParity(source), 4);
        assertEquals(PacketErrorDetector.checkEvenParity(source, 4), true);
    }
}
