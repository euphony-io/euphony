package co.euphony.tx;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@SmallTest
public class DataEncoderTest {
    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"b", "62"},
                {"c", "63"},
                {"abc", "616263"},
                {"lmno", "6c6d6e6f"},
                {"efg", "656667"},
                {"abcdefghijklmnopqrstuvwxyz", "6162636465666768696a6b6c6d6e6f707172737475767778797a"},
                {"ABC", "414243"},
                {"ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4142434445464748494a4b4c4d4e4f505152535455565758595a"},
        });
    }

    private String source;
    private String expectedEncodedResult;
    public DataEncoderTest(String source, String expectedEncodedResult) {
        this.source = source;
        this.expectedEncodedResult = expectedEncodedResult;
    }

    EuDataEncoder encoder;
    @Before
    public void setup() {
        encoder = new EuDataEncoder();
    }

    @Test
    /* encoding test */
    public void encoderTest() {
        encoder.setOriginalSource(source);
        assertEquals(source, encoder.getOriginalSource());
        assertEquals(expectedEncodedResult, encoder.encodeHexCharSource());
    }
}
