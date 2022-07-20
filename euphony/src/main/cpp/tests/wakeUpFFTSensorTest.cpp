#include <gtest/gtest.h>
#include <Definitions.h>
#include <FSK.h>
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
            .setCrossfade(FRONT)
            .build();

    auto floatWaveSourceVector = wave->getSource();
    float* floatWaveSource = &floatWaveSourceVector[0];

    sensor = std::make_unique<WakeUpFFTSensor>(sampleRate);
    int pos = sensor->feedAudioData(floatWaveSource, 2048);

    if(pos == -1)
        EXPECT_EQ(false, expectedWakeUp);
    else
        EXPECT_EQ(true, expectedWakeUp);

    EXPECT_EQ(pos, resultPos);
}

INSTANTIATE_TEST_SUITE_P(
        WakeUpTest,
        WakeUpFFTSensorTestFixture,
        ::testing::Values(
        /*
         * Frequency, FFTSize, sampleRate, expectedSpectrumIndex
         * kStartSignalFrequency = 17950
         */
        TestParamType(17924, true, 0, 44100),
        TestParamType(18300, true, 0, 44100),
        TestParamType(20000, false, -1, 44100)
        //TestParamType(20000, false, 2, 48000),
        //
        )
);