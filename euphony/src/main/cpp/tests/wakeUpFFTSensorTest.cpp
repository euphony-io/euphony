#include <gtest/gtest.h>
#include <Definitions.h>
#include <FSK.h>
#include <FFTProcessor.h>
#include <WaveBuilder.h>
#include <WakeUpFFTSensor.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, int, int, int> TestParamType;

class WakeUpFFTSensorTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::unique_ptr<WakeUpFFTSensor> sensor = nullptr;
};

TEST_P(WakeUpFFTSensorTestFixture, WakeUpFFTSensorTest)
{
    int inputFrequency;
    bool expectedWakeUp;
    std::tie(inputFrequency, expectedWakeUp) = GetParam();

    sensor = std::make_unique<WakeUpFFTSensor>()
    auto wave = Wave::create()
            .vibreatesAt(inputFrequency)
            .setSize(2048)
            .build();

    auto floatWaveSourceVector = wave->getSource();
    float* floatWaveSource = &floatWaveSourceVector[0];

    sensor->feedAudioData(floatWaveSource, 2048);
    bool isStarted = sensor->isWakeUp()

    EXPECT_EQ(isStarted, expectedWakeUp);
}

INSTANTIATE_TEST_SUITE_P(
        WakeUpTest,
        WakeUpFFTSensorTestFixture,
        ::testing::Values(
        /*
         * Frequency, FFTSize, sampleRate, expectedSpectrumIndex
         * kStartSignalFrequency = 17950
         */
        TestParamType(17924, true),
        TestParamType(18000, false)
        )
)