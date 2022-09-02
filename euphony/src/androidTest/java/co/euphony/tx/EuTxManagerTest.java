package co.euphony.tx;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import co.euphony.util.EuOption;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
@SmallTest
public class EuTxManagerTest {
    private static final String TX_TAG = "EU_TXMANAGER_TAG";

    String code;
    String expectedASCIICode;
    String expectedGenCode;
    int expectedStreamLength;
    int expectedBufferLength;

    EuTxManager txManager = null;
    public EuTxManagerTest(String code, String asciiCode, String expectedGenCode, int expectedStreamLength, int expectedBufferLength) {
        this.code = code;
        this.expectedASCIICode = asciiCode;
        this.expectedGenCode = expectedGenCode;
        this.expectedStreamLength = expectedStreamLength;
        this.expectedBufferLength = expectedBufferLength;
    }

    @Before
    public void setup() {
        txManager = new EuTxManager();
    }

    @Test
    public void getCode() {
        txManager.setCode(code);

        String activeResult = txManager.getCode();
        assertEquals(expectedASCIICode, activeResult);
    }

    @Test
    public void getGenCode() {
        txManager.setCode(code);

        String activeResult = txManager.getGenCode();
        assertEquals(expectedGenCode, activeResult);
    }

    @Test
    public void setGetCodeTest() {
        txManager.setCode("a");
        String activeResult = txManager.getGenCode();
        assertEquals("S6197", activeResult);

        txManager.setCode("b");
        activeResult = txManager.getGenCode();
        assertEquals("S6284", activeResult);

    }

    @Test
    public void getGenWaveSource() {
        txManager.setCode(code);
        txManager.setMode(EuOption.ModeType.DEFAULT);
        float[] waveSourceData = txManager.getOutStream();
        assertEquals(waveSourceData.length, expectedStreamLength);
    }

    @Test
    public void testRun() {
        txManager.callEuPI(18000, EuTxManager.EuPIDuration.LENGTH_SHORT);
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"a", "61", "S6197", 5*2048, 5*8},
                {"b", "62", "S6284", 5*2048, 5*8},
                {"c", "63", "S6375", 5*2048, 5*8},
                {"abc", "616263", "S61626386", 9*2048, 9*8},
                {"Abc", "416263", "S416263a4", 9*2048, 9*8},
                {"lmno", "6c6d6e6f", "S6c6d6e6f20", 11*2048, 11*8},
                {"efg", "656667", "S656667c2", 9*2048, 9*8},
                {"abcdefghijklmnopqrstuvwxyz", "6162636465666768696a6b6c6d6e6f707172737475767778797a", "S6162636465666768696a6b6c6d6e6f707172737475767778797aaa", 55*2048, 55*8},
                {"ABC", "414243", "S414243e4", 9*2048, 9*8},
                {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4142434445464748494a4b4c4d4e4f505152535455565758595a", "S4142434445464748494a4b4c4d4e4f505152535455565758595aea", 55*2048, 55*8},
        });
    }
}