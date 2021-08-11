
#include <gtest/gtest.h>
#include <Definitions.h>
#include <Wave.h>
#include <string>
#include <tuple>
#include <regex>

using std::string;
using namespace Euphony;

typedef std::tuple<int, int> TestParamType;

class WaveTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void makeFrequency() {
        EXPECT_EQ(wave, nullptr);
        wave = new Wave();
        ASSERT_NE(wave, nullptr);
    }

    Wave* wave = nullptr;
};

TEST_P(WaveTestFixture, WaveUnitTest)
{
    makeFrequency();

    int inputHz;
    int inputSize;

    std::tie(inputHz, inputSize) = GetParam();

    wave->setHz(inputHz);
    EXPECT_EQ(wave->getHz(), inputHz);
    wave->setSize(inputSize);
    EXPECT_EQ(wave->getSize(), inputSize);

    std::vector<float> source = wave->getSource();
    EXPECT_EQ(wave->getSource().capacity(), inputSize);
    EXPECT_EQ(wave->getSource().size(), 0);

    wave->oscillate();
    EXPECT_EQ(wave->getSource().capacity(), inputSize);
    EXPECT_EQ(wave->getSource().size(), inputSize);
}

INSTANTIATE_TEST_CASE_P(
        WaveTestSuite,
        WaveTestFixture,
        ::testing::Values(
                TestParamType(18000, 512),
                TestParamType(18000, 1024),
                TestParamType(18000, 2048),
                TestParamType(20000, 2048),
                TestParamType(21000, 2048)
));