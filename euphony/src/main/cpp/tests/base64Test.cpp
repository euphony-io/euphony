#include <gtest/gtest.h>
#include <Definitions.h>
#include <Base64.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::vector<u_int8_t>, std::string> TestParamType;

class Base64TranslationFixture : public ::testing::TestWithParam<TestParamType> {

public:
    Base* base64 = nullptr;
};

TEST_P(Base64TranslationFixture, DefaultEncodingTest)
{
    std::vector<u_int8_t> source;
    std::string expectedEncodedResult;

    std::tie(source, expectedEncodedResult) = GetParam();
    HexVector hv = HexVector(source);
    base64 = new Base64(hv);
    std::string actualResult = base64->getBaseString();
    EXPECT_EQ(actualResult, expectedEncodedResult);
}

INSTANTIATE_TEST_SUITE_P(
        Base64TranslationTest,
        Base64TranslationFixture,
        ::testing::Values(
                TestParamType(std::vector<u_int8_t>{ 0x05 }, "F"),
                TestParamType(std::vector<u_int8_t>{ 0x60 }, "Bg"),
                TestParamType(std::vector<u_int8_t>{ 0x63 }, "Bj"),
                TestParamType(std::vector<u_int8_t>{ 0x99 }, "CZ"),
                TestParamType(std::vector<u_int8_t>{ 0x98 }, "CY"),
                TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "YWJj"),
                TestParamType(std::vector<u_int8_t>{ 0x6c, 0x6d, 0x6e, 0x6f }, "BsbW5v")
        ));