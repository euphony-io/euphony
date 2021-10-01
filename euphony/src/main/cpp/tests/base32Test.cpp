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
                TestParamType(std::vector<u_int8_t>{ 0x05 }, "5"),
                TestParamType(std::vector<u_int8_t>{ 0x60 }, "30"),
                TestParamType(std::vector<u_int8_t>{ 0x63 }, "33"),
                TestParamType(std::vector<u_int8_t>{ 0x99 }, "4p"),
                TestParamType(std::vector<u_int8_t>{ 0x98 }, "4o"),
                TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "62oj3"),
                TestParamType(std::vector<u_int8_t>{ 0x6c, 0x6d, 0x6e, 0x6f }, "1m6qrjf"),
                TestParamType(std::vector<u_int8_t>{ 0x65, 0x66, 0x67 }, "6apj7"),
                TestParamType(std::vector<u_int8_t>{
                        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
                        0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70,
                        0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79
                }, "30c5h66p35cpjmgqbaddm6qrjfe1on4srkelr7eu3p")
        ));