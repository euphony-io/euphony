#include <gtest/gtest.h>
#include <Definitions.h>
#include <FSK.h>
#include <FFTProcessor.h>
#include <WaveBuilder.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, int, int, int> TestParamType;

class FFTProcessorTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::unique_ptr<FFTModel> fft = nullptr;

    int getResultByFFT(const int inputFrequency, const int sampleRate, const int fftSize) const {
        auto wave = Wave::create()
                .vibratesAt(inputFrequency)
                .setSize(512)
                .build();

        auto waveSourceVector = wave->getSource();
        float *floatWaveSource = &waveSourceVector[0];
        auto result = fft->makeSpectrum(floatWaveSource);

        return FSK::getMaxIdxFromSource(result.amplitudeSpectrum, 32, sampleRate, fftSize);
    }
};

TEST_P(FFTProcessorTestFixture, FFTProcessorTest)
{
    int inputFrequency, inputFFTSize, inputSampleRate, expectedSpectrumIndex;
    std::tie(inputFrequency, inputFFTSize, inputSampleRate, expectedSpectrumIndex) = GetParam();

    fft = std::make_unique<FFTProcessor>(inputFFTSize, inputSampleRate);

    const int startFrequency = inputFrequency;
    for(int i = 0; i < 32; i++){
        /* Current Frequency Check*/
        int activeResult = getResultByFFT(inputFrequency, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        /* Minimum Frequency(-41) Check*/
        activeResult = getResultByFFT(inputFrequency - 41, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        /* Maximum Frequency(+43) Check*/
        activeResult = getResultByFFT(inputFrequency + 43, inputSampleRate, inputFFTSize);
        EXPECT_EQ(expectedSpectrumIndex, activeResult);

        inputFrequency = (int)(((float)inputSampleRate / (float)inputFFTSize) * (float)(i + 1)) + startFrequency;
        expectedSpectrumIndex++;
    }
    /* TODO: Need to expect equal with phase spectrum's result */
}

INSTANTIATE_TEST_SUITE_P(
        FFTTest,
        FFTProcessorTestFixture,
        ::testing::Values(
                /*
                 * Frequency, FFTSize, sampleRate, expectedSpectrumIndex
                 * kStartSignalFrequency = 17915
                 */
                TestParamType(17915, 512, 44100, -1)
        )
);