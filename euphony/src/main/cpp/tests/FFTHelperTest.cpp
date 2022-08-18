#include <gtest/gtest.h>
#include <FFTHelper.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, int, int, int> TestParamType;

class FFTHelperTestFixture : public ::testing::TestWithParam<TestParamType> {
public:
    std::unique_ptr<FFTHelper> fftHelper = nullptr;
};

TEST_P(FFTHelperTestFixture, FFTHelperTest)
{
    int inputFFTSize, inputSampleRate, inputStandardFrequency, inputExpectedFFTIdx;
    std::tie(inputFFTSize, inputSampleRate, inputStandardFrequency, inputExpectedFFTIdx) = GetParam();
    fftHelper = std::make_unique<FFTHelper>(inputFFTSize, inputSampleRate, inputStandardFrequency);
    EXPECT_EQ(inputExpectedFFTIdx, fftHelper->getIndexOfStandardFrequency());
    EXPECT_EQ(inputExpectedFFTIdx, FFTHelper::getIndexOfStandardFrequency(inputStandardFrequency, inputFFTSize, inputSampleRate));
}

INSTANTIATE_TEST_SUITE_P(
        FFTHelperTest,
        FFTHelperTestFixture,
        ::testing::Values(
            /*
             * fftSize, sampleRate, frequency, expectedResultIndex
             * kStartSignalFrequency = 17915
             */
            TestParamType(512, 44100, 18001, 209),
            TestParamType(1024, 44100, 18001, 418),
            TestParamType(512, 48000, 18000, 192),
            TestParamType(1024, 48000, 18000, 384)
            )
);