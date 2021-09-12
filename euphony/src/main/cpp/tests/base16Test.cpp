#include <gtest/gtest.h>
#include <Definitions.h>
#include <Base16.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::vector<u_int8_t>, std::string> TestParamType;

class Base16TranslationFixture : public ::testing::TestWithParam<TestParamType> {

public:
    Base* base16 = nullptr;
};

TEST_P(Base16TranslationFixture, DefaultEncodingTest)
{
    std::vector<u_int8_t> source;
    std::string expectedEncodedResult;

    std::tie(source, expectedEncodedResult) = GetParam();
    HexVector hv = HexVector(source);
    base16 = new Base16(hv);
    std::string actualResult = base16->getBaseString();
    EXPECT_EQ(actualResult, expectedEncodedResult);
}

INSTANTIATE_TEST_SUITE_P(
        Base16TranslationTest,
        Base16TranslationFixture,
        ::testing::Values(
                TestParamType(std::vector<u_int8_t>{ 0x61 }, "61"),
                TestParamType(std::vector<u_int8_t>{ 0x62 }, "62"),
                TestParamType(std::vector<u_int8_t>{ 0x63 }, "63"),
                TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "616263"),
                TestParamType(std::vector<u_int8_t>{ 0x6c, 0x6d, 0x6e, 0x6f }, "6c6d6e6f"),
                TestParamType(std::vector<u_int8_t>{ 0x65, 0x66, 0x67 }, "656667"),
                TestParamType(std::vector<u_int8_t>{
                    0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
                    0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70,
                    0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a
                    }, "6162636465666768696a6b6c6d6e6f707172737475767778797a"),
                TestParamType(std::vector<u_int8_t>{ 0x41, 0x42, 0x43 }, "414243"),
                TestParamType(std::vector<u_int8_t>{
                    0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48,
                    0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50,
                    0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a
                }, "4142434445464748494a4b4c4d4e4f505152535455565758595a")
        ));