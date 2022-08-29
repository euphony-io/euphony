#include <gtest/gtest.h>
#include <Definitions.h>
#include <base/Base2.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::vector<u_int8_t>, std::string> TestParamType;

class Base2TranslationFixture : public ::testing::TestWithParam<TestParamType> {
public:
    Base* base2 = nullptr;
};

TEST_P(Base2TranslationFixture, DefaultBase2Test)
{
    std::vector<u_int8_t> source;
    std::string expectedEncodedResult;

    std::tie(source, expectedEncodedResult) = GetParam();
    HexVector hv = HexVector(source);
    base2 = new Base2(hv);
    std::string actualResult = base2->getBaseString();
    EXPECT_EQ(actualResult, expectedEncodedResult);
}

INSTANTIATE_TEST_SUITE_P(
        Base2TranslationTest,
        Base2TranslationFixture,
        ::testing::Values(
        TestParamType(std::vector<u_int8_t>{ 0x61 }, "01100001"),
        TestParamType(std::vector<u_int8_t>{ 0x62 }, "01100010"),
        TestParamType(std::vector<u_int8_t>{ 0x63 }, "01100011"),
        TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "011000010110001001100011"),
        TestParamType(std::vector<u_int8_t>{ 0x78, 0x79, 0x7a }, "011110000111100101111010")
));