package co.euphony.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilUnitTest {
    /* that would be substituted by packetErrorDetector.cpp on gtest */

    @Test
    public void packet_err_detect_isCorrect()
    {
        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};

        assertEquals(PacketErrorDetector.makeCheckSum(source), 14);
        assertEquals(PacketErrorDetector.makeCheckSum(   234), 6);
        assertEquals(PacketErrorDetector.makeParallelParity(source), 4);
        assertEquals(PacketErrorDetector.makeParallelParity(234), 10);
        assertTrue(PacketErrorDetector.verifyCheckSum(source, 14));
        assertFalse(PacketErrorDetector.verifyCheckSum(source, 13));
        assertTrue(PacketErrorDetector.verifyEvenParity(source, 4));
        assertFalse(PacketErrorDetector.verifyEvenParity(source, 5));

        //assertEquals(PacketErrorDetector.makeErrorDetectionCode(EuDataEncoder.encodeStaticHexCharSource("hello"), EuOption.EncodingType.HEX), "e4");
    }

    @Test
    public void error_handler_isCorret()
    {
        ErrorHandler mErrorHandler = new ErrorHandler(20);
        mErrorHandler.euSetChannelState(ErrorHandler.BUSY);
        assertEquals(mErrorHandler.euGetChannelState(), ErrorHandler.BUSY);
        mErrorHandler.euSetChannelState(ErrorHandler.UNBUSY);
        assertEquals(mErrorHandler.euGetChannelState(), ErrorHandler.UNBUSY);

        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
        assertEquals(mErrorHandler.checkNoise(source, 10), ErrorHandler.NONE);
    }

    @Test
    public void option_isCorrect()
    {
        EuOption option = new EuOption();
        option.setEncodingType(EuOption.EncodingType.HEX);
        assertEquals(option.getEncodingType(), EuOption.EncodingType.HEX);
        option.setCommunicationMode(EuOption.CommunicationMode.GENERAL);
        assertEquals(option.getCommunicationMode(), EuOption.CommunicationMode.GENERAL);
    }

}
