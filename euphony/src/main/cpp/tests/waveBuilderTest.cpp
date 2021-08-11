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
    shared_ptr<Wave> wave = nullptr;
};

TEST_P(WaveBuilderTestFixture, WaveBuilderUnitTest)
{
int inputHz;
int inputSize;

std::tie(inputHz, inputSize) = GetParam();

wave = Wave::create()
        .vibratesAt(inputHz)
        .setSize(inputSize)
        .build();

EXPECT_EQ(wave->getHz(), inputHz);
EXPECT_EQ(wave->getSize(), inputSize);
EXPECT_EQ(wave->getSource().size(), inputSize);
}

INSTANTIATE_TEST_CASE_P(
        WaveBuilderTestSuite,
        WaveBuilderTestFixture,
        ::testing::Values(
        TestParamType(18000, 512),
        TestParamType(18000, 1024),
        TestParamType(18000, 2048),
        TestParamType(20000, 2048),
        TestParamType(21000, 2048)
));
