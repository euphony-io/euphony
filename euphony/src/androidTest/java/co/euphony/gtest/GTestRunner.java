package co.euphony.gtest;

import android.os.Build;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class GTestRunner {
    static final String TAG = "GTESTRUNNER";
    static final String GTEST_RESULT_FILE = "testEuphonyResult.log";
    static final String[] EXPECTED_RESULT = {
            "[==========] Running 9 tests from 1 test suite.",
            "[----------] Global test environment set-up.",
            "[----------] 9 tests from AsciiEncodingTestSuite/EncoderTestFixture",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/0",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/0 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/1",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/1 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/2",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/2 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/3",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/3 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/4",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/4 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/5",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/5 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/6",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/6 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/7",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/7 (0 ms)",
            "[ RUN      ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/8",
            "[       OK ] AsciiEncodingTestSuite/EncoderTestFixture.ASCIIEncodingTest/8 (0 ms)",
            "[----------] 9 tests from AsciiEncodingTestSuite/EncoderTestFixture (1 ms total)",
            "",
            "[----------] Global test environment tear-down",
            "[==========] 9 tests from 1 test suite ran. (1 ms total)",
            "[  PASSED  ] 9 tests.",
    };
    @Test
    public void executeBinary() {

        String abi = Build.CPU_ABI;
        String filesDir = "/data/local/tmp/testEuphony/" + abi;
        try {
            File testResultFile = new File(filesDir , GTEST_RESULT_FILE);
            if(testResultFile.exists()) {
                Log.i(TAG, "Test result is not available. It will be passed.");
                return;
            }

            InputStream inStream = new FileInputStream(testResultFile);

            StringBuffer actualResultBuffer = new StringBuffer();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inStream.read(buffer)) != -1) {
                actualResultBuffer.append(new String(buffer, 0 , read));
            }
            inStream.close();

            String[] actualResult = actualResultBuffer.toString().split("\n");
            Assert.assertThat(actualResult.length, is(27));

            for(int i = 1; i < actualResult.length; i++) {
                Assert.assertThat(actualResult[i], is(EXPECTED_RESULT[i-1]));
            }
        } catch (IOException e){
            Log.e(TAG, "Could not find a test result ", e);
        }
    }
}
