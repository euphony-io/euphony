package co.euphony.rx;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@SmallTest
public class DataDecoderTest {
    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"62", "b"},
                {"63", "c"},
                {"616263", "abc"},
                {"6c6d6e6f", "lmno"},
                {"656667", "efg"},
                {"6162636465666768696a6b6c6d6e6f707172737475767778797a", "abcdefghijklmnopqrstuvwxyz"},
                {"414243", "ABC"},
                {"4142434445464748494a4b4c4d4e4f505152535455565758595a", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"},
        });
    }

    private String source;
    private String expectedDecodedResult;
    public DataDecoderTest(String source, String expectedDecodedResult) {
        this.source = source;
        this.expectedDecodedResult = expectedDecodedResult;
    }

    EuDataDecoder decoder;
    @Before
    public void setup() {
        decoder = new EuDataDecoder("60");
    }

    @Test
    public void decoderTest() {
        decoder.setOriginalSource(source);
        assertEquals(source, decoder.getOriginalSource());
        assertEquals(expectedDecodedResult, decoder.decodeHexCharSource());
    }
}
