#include <gtest/gtest.h>
#include <Definitions.h>
#include <Base32.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::vector<u_int8_t>, std::string> TestParamType;

class Base32TranslationFixture : public ::testing::TestWithParam<TestParamType> {

public:
    Base* base32 = nullptr;
};

TEST_P(Base32TranslationFixture, DefaultEncodingTest)
{
    std::vector<u_int8_t> source;
    std::string expectedEncodedResult;

    std::tie(source, expectedEncodedResult) = GetParam();
    HexVector hv = HexVector(source);
    base32 = new Base32(hv);
    std::string actualResult = base32->getBaseString();
    EXPECT_EQ(actualResult, expectedEncodedResult);
}

INSTANTIATE_TEST_SUITE_P(
        Base32TranslationTest,
        Base32TranslationFixture,
        ::testing::Values(
                TestParamType(std::vector<u_int8_t>{ 0x61 }, "31"),
                TestParamType(std::vector<u_int8_t>{ 0x62 }, "32"),
                TestParamType(std::vector<u_int8_t>{ 0x63 }, "33"),
                TestParamType(std::vector<u_int8_t>{ 0x99 }, "4p"),
                TestParamType(std::vector<u_int8_t>{ 0x98 }, "4o"),
                TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "313233"),
                TestParamType(std::vector<u_int8_t>{ 0x6c, 0x6d, 0x6e, 0x6f }, "3c3d3e3f"),
                TestParamType(std::vector<u_int8_t>{ 0x65, 0x66, 0x67 }, "353637")
        ));