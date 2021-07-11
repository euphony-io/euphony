package co.euphony.common;

import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import co.euphony.util.PacketErrorDetector;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@SmallTest
public class PacketErrorDetectorTest {
    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"62", 0x8, 0x4},
                {"63", 0x7, 0x5},
                {"616263", 0x8, 0x6},
                {"6c6d6e6f", 0x2, 0x0},
                {"656667", 0xc, 0x2},
                {"6162636465666768696a6b6c6d6e6f707172737475767778797a", 0xa, 0xa},
                {"414243", 0xe, 0x4},
                {"4142434445464748494a4b4c4d4e4f505152535455565758595a", 0xe, 0xa},
        });
    }

    private String encodedData;
    private int expectedChecksum;
    private int expectedParity;
    public PacketErrorDetectorTest(String encodedData, int expectedChecksum, int expectedParity )
    {
        this.encodedData = encodedData;
        this.expectedChecksum = expectedChecksum;
        this.expectedParity = expectedParity;
    }

    @Before
    public void setup() { }

    @Test
    public void checksumTest() {
        int actualChecksum = PacketErrorDetector.makeCheckSum(encodedData);
        assertEquals(expectedChecksum, actualChecksum);
    }

    @Test
    public void parallelParityTest() {
        int actualParity = PacketErrorDetector.makeParallelParity(encodedData);
        assertEquals(expectedParity, actualParity);
    }
}
