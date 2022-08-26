#include <gtest/gtest.h>
#include <FFTProcessor.h>
#include <FFTHelper.h>
#include <WaveBuilder.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, int, int, int> TestParamType;

class FFTHelperTestFixture : public ::testing::TestWithParam<TestParamType> {
public:
    std::unique_ptr<FFTHelper> fftHelper = nullptr;
    std::unique_ptr<FFTProcessor> fft = nullptr;
};

TEST_P(FFTHelperTestFixture, FFTHelperTest)
{
    int inputFFTSize, inputSampleRate, inputStandardFrequency, inputExpectedFFTIdx;
    std::tie(inputFFTSize, inputSampleRate, inputStandardFrequency, inputExpectedFFTIdx) = GetParam();

    /* TEST FOR GETTING INDEX OF STANDARD FREQUENCY */
    fftHelper = std::make_unique<FFTHelper>(inputFFTSize, inputSampleRate, inputStandardFrequency);
    int activeIdx = fftHelper->getIndexOfStandardFrequency();
    EXPECT_EQ(inputExpectedFFTIdx, activeIdx);
    activeIdx = FFTHelper::getIndexOfStandardFrequency(inputStandardFrequency, inputFFTSize, inputSampleRate);
    EXPECT_EQ(inputExpectedFFTIdx, activeIdx);

    /* TEST FOR GETTING MAX IDX FROM FFT SOURCE */
    auto wave = Wave::create()
            .vibratesAt(inputStandardFrequency)
            .setSampleRate(inputSampleRate)
            .setSize(1024)
            .build();
    auto floatWaveSourceVector = wave->getSource();
    float* floatWaveSource = &floatWaveSourceVector[0];

    fft = std::make_unique<FFTProcessor>(inputFFTSize, inputSampleRate);
    auto result = fft->makeSpectrum(floatWaveSource);
    activeIdx = FFTHelper::getMaxIdxFromSource(result.amplitudeSpectrum, inputStandardFrequency, 16, inputFFTSize, inputSampleRate);
    EXPECT_EQ(0, activeIdx);
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
            TestParamType(32, 44100, 18001, 13),
            TestParamType(1024, 44100, 18001, 418),
            TestParamType(512, 48000, 18000, 192),
            TestParamType(1024, 48000, 18000, 384)
            )
);