#include <gtest/gtest.h>
#include <Definitions.h>
#include <base/Base64.h>
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
                TestParamType(std::vector<u_int8_t>{ 0xa8, 0xe9 }, "Kjp"),
                TestParamType(std::vector<u_int8_t>{ 0xab, 0xcd }, "KvN"),
                TestParamType(std::vector<u_int8_t>{ 0x3d, 0x2e }, "D0u"),
                TestParamType(std::vector<u_int8_t>{ 0x4d, 0x5b }, "E1b"),
                TestParamType(std::vector<u_int8_t>{ 0x9a, 0xc7 }, "JrH"),
                TestParamType(std::vector<u_int8_t>{ 0xaa, 0xbb }, "Kq7"),
                TestParamType(std::vector<u_int8_t>{ 0x61, 0x62, 0x63 }, "YWJj"),
                TestParamType(std::vector<u_int8_t>{ 0x6c, 0x6d, 0x6e, 0x6f }, "BsbW5v"),
                TestParamType(std::vector<u_int8_t>{ 0x12, 0x34, 0x5a, 0x9f }, "SNFqf"),
                TestParamType(std::vector<u_int8_t>{ 0x67, 0x6f, 0x6f, 0x64 }, "Bnb29k"),
                TestParamType(std::vector<u_int8_t>{ 0x10, 0x0, 0x0, 0x0, 0x0, 0x0 }, "BAAAA"),
                TestParamType(std::vector<u_int8_t>{ 0xc0, 0xde, 0xba, 0x5e, 0xba, 0x11 }, "wN66XroR"),
                TestParamType(std::vector<u_int8_t>{ 0x6f, 0x3c, 0x4e, 0x5a, 0x6b, 0x2d, 0x1c, 0xaa }, "G88TlprLRyq"),
                TestParamType(std::vector<u_int8_t>{ 0x9f, 0x1b, 0x3c, 0x44, 0x55, 0x1d, 0x99, 0x80, 0x72, 0x17, 0x22, 0xcf  }, "nxs8RFUdmYByFyLP")

        ));