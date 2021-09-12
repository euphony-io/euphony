package euphony.lib.util;

import org.junit.Test;

import co.euphony.util.ErrorHandler;
import co.euphony.util.EuOption;
import co.euphony.util.PacketErrorDetector;

import static org.junit.Assert.assertEquals;
public class UtilUnitTest {
    @Test
    public void packet_err_detect_isCorrect()
    {
        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
        assertEquals(PacketErrorDetector.makeCheckSum(source), 14);
        assertEquals(PacketErrorDetector.makeCheckSum(   234), 6);
        assertEquals(PacketErrorDetector.makeParellelParity(source), 4);
        assertEquals(PacketErrorDetector.makeParellelParity(234), 10);
        assertEquals(PacketErrorDetector.checkEvenParity(source, 4), true);
        assertEquals(PacketErrorDetector.checkEvenParity(source, 5), false);
        assertEquals(PacketErrorDetector.verifyCheckSum(source, 14), true);
        assertEquals(PacketErrorDetector.verifyCheckSum(source, 13), false);

        PacketErrorDetector errorDetector = new PacketErrorDetector();
        assertEquals(errorDetector.euGetEvenParityState(), false);
        errorDetector.euSetEvenParityState(true);
        assertEquals(errorDetector.euGetEvenParityState(), true);
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
        option.setEncodingType(EuOption.EncodingType.ASCII);
        assertEquals(option.getEncodingType(), EuOption.EncodingType.ASCII);
        option.setEncodingType(EuOption.EncodingType.HEX);
        assertEquals(option.getEncodingType(), EuOption.EncodingType.HEX);
        option.setCommunicationMode(EuOption.CommunicationMode.GENERAL);
        assertEquals(option.getCommunicationMode(), EuOption.CommunicationMode.GENERAL);
        option.setCommunicationMode(EuOption.CommunicationMode.LIVE);
        assertEquals(option.getCommunicationMode(), EuOption.CommunicationMode.LIVE);

        EuOption option2 = new EuOption(EuOption.EncodingType.HEX, EuOption.CommunicationMode.LIVE);
        assertEquals(option2.getEncodingType(), EuOption.EncodingType.HEX);
        assertEquals(option2.getCommunicationMode(), EuOption.CommunicationMode.LIVE);
    }

}
