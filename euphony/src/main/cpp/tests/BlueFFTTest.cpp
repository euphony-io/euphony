#include <gtest/gtest.h>
#include <Definitions.h>
#include <WaveBuilder.h>
#include <tuple>
#include <BlueFFT.h>
#include <FFTHelper.h>

using namespace Euphony;

typedef std::tuple<int, int, int, int, int> TestParamType;

class BlueFFTTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::unique_ptr<FFTModel> fft = nullptr;

    int getResultByFFT(const int inputFrequency, const int standardFrequency, const int sampleRate, const int fftSize) const {
        auto wave = Wave::create()
                .vibratesAt(inputFrequency)
                .setSize(fftSize)
                .setSampleRate(sampleRate)
                .build();

        auto waveSourceVector = wave->getSource();
        float *floatWaveSource = &waveSourceVector[0];
        auto result = fft->makeSpectrum(floatWaveSource);

        return FFTHelper::getMaxIdxFromSource(result.amplitudeSpectrum, standardFrequency, 32, fftSize, sampleRate);
    }

};

/* Floating Point Source Test */
TEST_P(BlueFFTTestFixture, BlueFFTTest)
{
    int inputFrequency, inputFFTSize, inputSampleRate, inputStandardFrequency, expectedSpectrumIndex;
    std::tie(inputFrequency, inputFFTSize, inputSampleRate, inputStandardFrequency, expectedSpectrumIndex) = GetParam();

    fft = std::make_unique<BlueFFT>(inputFFTSize, inputSampleRate);

    const int startFrequency = inputFrequency;
    const int frequencyRange = (inputSampleRate / inputFFTSize / 2);
    const int frequencyApproximateRange = frequencyRange - (frequencyRange % 10);
    for(int i = 0; i < 32; i++){
        /* Current Frequency Check*/
        int activeResult = getResultByFFT(inputFrequency, inputStandardFrequency, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        /* Minimum Frequency Check*/
        activeResult = getResultByFFT(inputFrequency - frequencyApproximateRange, inputStandardFrequency, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        /* Maximum Frequency Check*/
        activeResult = getResultByFFT(inputFrequency + frequencyApproximateRange, inputStandardFrequency, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        inputFrequency = (int)(((float)inputSampleRate / (float)inputFFTSize) * (float)(i + 1)) + startFrequency;
        expectedSpectrumIndex++;
    }
    /* TODO: Need to expect equal with phase spectrum's result */
}

INSTANTIATE_TEST_SUITE_P(
        FFTTest,
        BlueFFTTestFixture,
        ::testing::Values(
                /*
                 * Frequency, FFTSize, sampleRate, standardFrequency, expectedSpectrumIndex
                 * kStartSignalFrequency = 17915
                 */
                TestParamType(17915, 512, 44100, 18001, -1),
                TestParamType(17959, 1024, 44100, 18001, -1),
                TestParamType(17906, 512, 48000, 18000, -1),
                TestParamType(17953, 1024, 48000, 18000, -1)
        )
);