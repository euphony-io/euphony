#include <gtest/gtest.h>
#include <Definitions.h>
#include <WaveBuilder.h>
#include <string>
#include <tuple>
#include <regex>

using std::string;
using namespace Euphony;

typedef std::tuple<int, int> TestParamType;

class WaveBuilderTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::shared_ptr<Wave> wave = nullptr;
};

TEST_P(WaveBuilderTestFixture, WaveBuilderUnitTest)
{
    int inputHz;
    int inputSize;

    std::tie(inputHz, inputSize) = GetParam();

    wave = Wave::create()
            .vibratesAt(inputHz)
            .setSize(inputSize)
            .setSampleRate(kSampleRate)
            .build();

    EXPECT_EQ(wave->getHz(), inputHz);
    EXPECT_EQ(wave->getSize(), inputSize);
    EXPECT_EQ(wave->getSource().size(), inputSize);

    /* TODO: WaveBuilder should be tested below
     * 1) Result of WaveBuilder is correct?
     * 2) smpaleRate comparision
     * 3) what about fail case of WaveBuilder
     * */
}

INSTANTIATE_TEST_SUITE_P(
        WaveBuilderTest,
        WaveBuilderTestFixture,
        ::testing::Values(
        TestParamType(18000, 512),
        TestParamType(18000, 1024),
        TestParamType(18000, 2048),
        TestParamType(20000, 2048),
        TestParamType(21000, 2048)
));
