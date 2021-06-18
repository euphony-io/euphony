package euphony.lib.transmitter;

import org.junit.Test;

import euphony.tx.EuCodeMaker;
import euphony.tx.EuDataEncoder;
import euphony.util.EuOption;
import euphony.tx.EuTxManager;

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
        mEuTxManager.process();
        mEuTxManager.process(3);

        EuTxManager mEuTxManager2 = new EuTxManager(new EuOption(EuOption.EncodingType.ASCII, EuOption.CommunicationMode.LIVE, EuOption.ModulationType.FSK));
        mEuTxManager2.setCode("Hello, Euphony");
        length = 253952;
        outStreamLength = mEuTxManager2.getOutStream().length;
        assertEquals(outStreamLength, length);
    }

    @Test
    public void EuCodeMaker_iscorrect() {
        EuOption txOption = new EuOption();
        txOption.setModulationType(EuOption.ModulationType.CPFSK);

        String code = EuDataEncoder.encodeStaticHexCharSource("hello");
        EuCodeMaker mCodeMaker = new EuCodeMaker(txOption);
        short[] musicSource = mCodeMaker.assembleData(code);
        assertEquals(musicSource.length, 5 * 2 * 2048);

        txOption.setModulationType(EuOption.ModulationType.FSK);
        mCodeMaker.setOption(txOption);
        musicSource = mCodeMaker.assembleData(code);
        assertEquals(musicSource.length, 5 * 2 * 2048);

        txOption.setModulationType(EuOption.ModulationType.ASK);
        mCodeMaker.setOption(txOption);
        code = EuDataEncoder.encodeStaticBinaryCharSource("hello");
        musicSource = mCodeMaker.assembleData(code);
        assertEquals(musicSource.length, 5*2*4*2048);
    }
}
