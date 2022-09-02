#include <gtest/gtest.h>
#include <Definitions.h>
#include <base/Base32.h>
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
                TestParamType(std::vector < u_int8_t > {0x05}, "5"),
                TestParamType(std::vector < u_int8_t > {0x60}, "30"),
                TestParamType(std::vector < u_int8_t > {0x63}, "33"),
                TestParamType(std::vector < u_int8_t > {0x99}, "4p"),
                TestParamType(std::vector < u_int8_t > {0x98}, "4o"),
                TestParamType(std::vector < u_int8_t > {0x05, 0x06, 0x07}, "1b7"),
                TestParamType(std::vector < u_int8_t > {0x61, 0x62, 0x63}, "62oj3"),
                TestParamType(std::vector < u_int8_t > {0xff, 0xee, 0xdd, 0xcc}, "3vutnec"),
                TestParamType(std::vector < u_int8_t > {0x01, 0x17, 0x56, 0xb8}, "hello"),
                TestParamType(std::vector < u_int8_t > {0x7c, 0xa1, 0x1e, 0xca, 0xf0}, "fighting"),
                TestParamType(std::vector < u_int8_t > {0xf2, 0xbb, 0x2f, 0x5a, 0x11}, "uatiumgh"),
                TestParamType(std::vector < u_int8_t > {0x18, 0xef, 0x5e, 0xfe, 0x71, 0xd7}, "ottffssen"),
                TestParamType(std::vector < u_int8_t > {0xf1, 0x2d, 0xa9, 0x50, 0x89, 0xcc}, "7h5mkl12ec"),
                TestParamType(std::vector < u_int8_t > {0x11, 0x0, 0xC8, 0x53, 0x1D, 0x0, 0x9}, "123456789"),
                TestParamType(std::vector < u_int8_t > {0x10, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 }, "100000000"),
                TestParamType(std::vector < u_int8_t > {
                    0x06, 0xdd, 0x9f, 0x2e, 0xb2,
                    0x66, 0x1f, 0x4c, 0x6d, 0xca
                    }, "republicofkorea"),
                TestParamType(std::vector<u_int8_t>{
                    0x14, 0xb6, 0x35, 0xcf, 0x84,
                    0x65, 0x3a, 0x56, 0xd7, 0xc6,
                    0x75, 0xbe, 0x77, 0xdf
                    },  "abcdefghijklmnopqrstuv"),
                TestParamType(std::vector < u_int8_t > {
                        0x60, 0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68,
                        0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70,
                        0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79
                }, "30c5h66p35cpjmgqbaddm6qrjfe1on4srkelr7eu3p")
        ));