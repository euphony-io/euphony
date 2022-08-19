#include <gtest/gtest.h>
#include <Definitions.h>
#include <FFTProcessor.h>
#include <WaveBuilder.h>
#include <WakeUpFFTSensor.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, bool, int, int> TestParamType;

class WakeUpFFTSensorTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::unique_ptr<WakeUpFFTSensor> sensor = nullptr;
};

TEST_P(WakeUpFFTSensorTestFixture, WakeUpFFTSensorTest)
{
    int inputFrequency;
    bool expectedWakeUp;
    int resultPos;
    int sampleRate;
    std::tie(inputFrequency, expectedWakeUp, resultPos, sampleRate) = GetParam();

    auto wave = Wave::create()
            .vibratesAt(inputFrequency)
            .setSize(2048)
            .setSampleRate(sampleRate)
            .setCrossfade(FRONT)
            .build();

    auto floatWaveSourceVector = wave->getSource();
    float* floatWaveSource = &floatWaveSourceVector[0];

    sensor = std::make_unique<WakeUpFFTSensor>(sampleRate);
    bool activeResult = sensor->detectWakeUpSign(floatWaveSource, 2048);

    EXPECT_EQ(activeResult, expectedWakeUp);
}

INSTANTIATE_TEST_SUITE_P(
        WakeUpTest,
        WakeUpFFTSensorTestFixture,
        ::testing::Values(
        /*
         * Frequency, FFTSize, sampleRate, expectedSpectrumIndex
         * kStartSignalFrequency = 18001 - 86
         */
        TestParamType(17915, true, 0, 44100),
        TestParamType(18000, false, 0, 44100),
        TestParamType(19000, false, -1, 44100),
        TestParamType(20000, false, -1, 44100)
        )
);