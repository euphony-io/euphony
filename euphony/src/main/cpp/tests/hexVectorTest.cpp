#include <gtest/gtest.h>
#include <tuple>
#include <HexVector.h>

using namespace Euphony;

typedef std::tuple<std::vector<u_int8_t>, std::string> TestParamType;

class HexVectorTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void createHexVector() {
        EXPECT_EQ(hexVector, nullptr);
        hexVector = new HexVector(0);
        ASSERT_NE(hexVector, nullptr);
    }

    HexVector* hexVector = nullptr;
};

TEST_P(HexVectorTestFixture, DefaultHex2StringTest)
{
    createHexVector();

    std::vector<u_int8_t> source;
    std::string expectedToStringResult;

    std::tie(source, expectedToStringResult) = GetParam();
    hexVector->setHexSource(source);

    EXPECT_EQ(hexVector->toString(), expectedToStringResult);
}

INSTANTIATE_TEST_SUITE_P(
        hexVectorTest,
        HexVectorTestFixture,
        ::testing::Values(
                TestParamType(std::vector<u_int8_t> { 0 }, "0"),
                TestParamType(std::vector<u_int8_t> { 1 }, "1"),
                TestParamType(std::vector<u_int8_t> { 2 }, "2"),
                TestParamType(std::vector<u_int8_t> { 0, 1 }, "01"),
                TestParamType(std::vector<u_int8_t> { 0x01 }, "1"),
                TestParamType(std::vector<u_int8_t> { 0x01, 0x0f }, "1f"),
                TestParamType(std::vector<u_int8_t> { 1, 2 }, "12"),
                TestParamType(std::vector<u_int8_t> { 1, 3, 5 }, "135"),
                TestParamType(std::vector<u_int8_t> { 1, 0xa, 0xf }, "1af"),
                TestParamType(std::vector<u_int8_t> { 0xa, 0xb, 0xc, 0xd, 0xe, 0xf }, "abcdef"),
                TestParamType(std::vector<u_int8_t> { 0x1f, 0x2f, 0x3f, 0x4f }, "1f2f3f4f"),
                TestParamType(std::vector<u_int8_t> { 0xff, 0xff, 0xff, 0x4f }, "ffffff4f")
        )
);