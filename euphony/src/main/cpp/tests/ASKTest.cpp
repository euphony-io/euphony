#include <gtest/gtest.h>
#include <Definitions.h>
#include <ASK.h>
#include <tuple>
#include <ASCIICharset.h>
#include <HexVector.h>
#include <Packet.h>

using namespace Euphony;

typedef std::tuple<std::string, int> TestParamType;

class ASKTestFixture : public ::testing::TestWithParam<TestParamType>{

public:
    void createASK() {
        EXPECT_EQ(ask, nullptr);
        ask = new ASK();
        ASSERT_NE(ask, nullptr);
    }

    ASK* ask = nullptr;
};

TEST_P(ASKTestFixture, ASKModulationStringTest)
{
    createASK();

    string inputCode;
    int expectedCodeLength;

    std::tie(inputCode, expectedCodeLength) = GetParam();

    auto modulateAsk = ask->modulate(inputCode);
    EXPECT_EQ(modulateAsk.size(), expectedCodeLength);

    auto demodulateResult = ask->demodulate(modulateAsk);
    EXPECT_EQ(inputCode, demodulateResult->getPayloadStr());
}

INSTANTIATE_TEST_CASE_P(
        ASKTestSuite,
        ASKTestFixture,
        ::testing::Values(
                TestParamType("0", 1),
                TestParamType("1", 1),
                TestParamType("11", 2),
                TestParamType("010", 3),
                TestParamType("101", 3),
                TestParamType("11110000", 8)
));