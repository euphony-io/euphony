package co.euphony.tx;

import org.junit.Test;

import co.euphony.tx.EuCodeMaker;
import co.euphony.tx.EuDataEncoder;
import co.euphony.util.EuOption;
import co.euphony.tx.EuTxManager;

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
