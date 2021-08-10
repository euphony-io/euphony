
#include <gtest/gtest.h>
#include <Definitions.h>
#include <Wave.h>
#include <string>
#include <tuple>
#include <regex>

using std::string;
using namespace Euphony;

typedef std::tuple<int, int> TestParamType;

class FrequencyTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void makeFrequency() {
        EXPECT_EQ(frequency, nullptr);
        frequency = new Wave();
        ASSERT_NE(frequency, nullptr);
    }

    Wave* frequency = nullptr;
};

TEST_P(FrequencyTestFixture, FrequencyUnitTest)
{
    makeFrequency();

    int inputHz;
    int inputSize;

    std::tie(inputHz, inputSize) = GetParam();

    frequency->setHz(inputHz);
    EXPECT_EQ(frequency->getHz(), inputHz);
    frequency->setSize(inputSize);
    EXPECT_EQ(frequency->getSize(), inputSize);
}

INSTANTIATE_TEST_CASE_P(
        AsciiDecodingTestSuite,
        FrequencyTestFixture,
        ::testing::Values(
                TestParamType(18000, 512),
                TestParamType(18000, 1024),
                TestParamType(18000, 2048),
                TestParamType(20000, 2048),
                TestParamType(21000, 2048)
));





