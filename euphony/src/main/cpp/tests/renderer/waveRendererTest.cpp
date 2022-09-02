#include <gtest/gtest.h>
#include <Definitions.h>
#include <renderer/WaveRenderer.h>
#include <string>
#include <tuple>
#include <regex>
#include <modem/FSK.h>

using namespace Euphony;

typedef std::tuple<std::string, int, int> TestParamType;

class WaveRendererTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::shared_ptr<Wave> wave = nullptr;
    std::shared_ptr<WaveRenderer> waveRenderer = nullptr;
};

TEST_P(WaveRendererTestFixture, WaveBuilderUnitTest)
{
    std::string inputString;
    int expectedBufferSize;
    int expectedStartData;

    std::tie(inputString, expectedBufferSize, expectedStartData) = GetParam();

    auto fsk = new FSK();
    auto waveList = fsk->modulate(inputString);

    waveRenderer = WaveRenderer::getInstance();//std::make_unique<WaveRenderer>(waveList, 2);
    waveRenderer->setWaveList(waveList);

    const auto actualWaveSourceSize = waveRenderer->getWaveSourceSize();
    EXPECT_EQ(actualWaveSourceSize, expectedBufferSize);

    auto demodulateResult = fsk->demodulate(waveRenderer->getWaveSource(), actualWaveSourceSize, kBufferSize);
    EXPECT_EQ(inputString, demodulateResult->getPayloadStr());

    delete fsk;
}

INSTANTIATE_TEST_SUITE_P(
        WaveRendererTest,
        WaveRendererTestFixture,
        ::testing::Values(
                TestParamType("0", 2048, 0),
                TestParamType("1", 2048, 1),
                TestParamType("2", 2048, 2),
                TestParamType("3", 2048, 3),
                TestParamType("4", 2048, 4),
                TestParamType("5", 2048, 5),
                TestParamType("012345", 2048*6, 0),
                TestParamType("0123456789", 2048*10, 0),
                TestParamType("abcdef", 2048*6, 10),
                TestParamType("0123456789abcdef", 2048*16, 0)
        )
);