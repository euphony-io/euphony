package co.euphony.util;

import org.junit.Test;

import co.euphony.util.ErrorHandler;
import co.euphony.util.EuOption;
import co.euphony.util.PacketErrorDetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilUnitTest {
    @Test
    public void packet_err_detect_isCorrect()
    {
        int[] source = {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
        assertEquals(PacketErrorDetector.makeCheckSum(source), 14);
        assertEquals(PacketErrorDetector.makeCheckSum(   234), 6);
        assertEquals(PacketErrorDetector.makeParallelParity(source), 4);
        assertEquals(PacketErrorDetector.makeParallelParity(234), 10);
        assertTrue(PacketErrorDetector.checkEvenParity(source, 4));
        assertFalse(PacketErrorDetector.checkEvenParity(source, 5));
        assertTrue(PacketErrorDetector.verifyCheckSum(source, 14));
        assertFalse(PacketErrorDetector.verifyCheckSum(source, 13));

        PacketErrorDetector errorDetector = new PacketErrorDetector();
        assertFalse(errorDetector.euGetEvenParityState());
        errorDetector.euSetEvenParityState(true);
        assertTrue(errorDetector.euGetEvenParityState());
    }

    @Test
    public void error_handler_isCorrect()
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
        EuOption option = EuOption.builder()
                .modeWith(EuOption.ModeType.DEFAULT)
                .encodingWith(EuOption.CodingType.BASE16)
                .modulationWith(EuOption.ModulationType.FSK)
                .build();

        assertEquals(option.getCodingType(), EuOption.CodingType.BASE16);
        assertEquals(option.getMode(), EuOption.ModeType.DEFAULT);
    }

}
