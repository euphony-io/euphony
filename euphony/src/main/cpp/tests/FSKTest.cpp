#include <gtest/gtest.h>
#include <Definitions.h>
#include <FSK.h>
#include <FFTProcessor.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, int, int> TestParamType;

class FSKTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void createFSK() {
        EXPECT_EQ(fsk, nullptr);
        fsk = new FSK();
        ASSERT_NE(fsk, nullptr);
    }

    void createFFT() {
        EXPECT_EQ(fft, nullptr);
        fft = new FFTProcessor(kFFTSize, kSampleRate);
        ASSERT_NE(fft, nullptr);
    }

    FSK* fsk = nullptr;
    FFTProcessor* fft = nullptr;

};

TEST_P(FSKTestFixture, FSKModulationTest)
{
    createFSK();
    createFFT();

    string inputCode;
    int expectedCodeLength;
    int expectedFreqIndex;

    std::tie(inputCode, expectedCodeLength, expectedFreqIndex) = GetParam();

    auto resultFSK = fsk->modulate(inputCode);
    EXPECT_EQ(resultFSK.size(), expectedCodeLength);

    for(auto it = resultFSK.begin(); it != resultFSK.end(); it++) {
        auto vectorInt16Source = (*it)->getInt16Source();
        int16_t* int16Source = &vectorInt16Source[0];

        float *resultBuf = fft->makeSpectrum(int16Source);
        int resultBufSize = fft->getResultSize();
        EXPECT_EQ(resultBufSize, (kFFTSize >> 1) + 1);
        int idx = fsk->demodulate(resultBuf, resultBufSize);
        EXPECT_EQ(idx, expectedFreqIndex++);
    }

    fft->destroy();
}

INSTANTIATE_TEST_CASE_P(
        FSKTestSuite,
        FSKTestFixture,
        ::testing::Values(
                TestParamType("0", 1, 209),
                TestParamType("1", 1, 210),
                TestParamType("2", 1, 211),
                TestParamType("3", 1, 212),
                TestParamType("4", 1, 213),
                TestParamType("5", 1, 214),
                TestParamType("012345", 6, 209),
                TestParamType("0123456789", 10, 209),
                TestParamType("ABCDEF", 6, 219),
                TestParamType("0123456789ABCDEF", 16, 209)
));