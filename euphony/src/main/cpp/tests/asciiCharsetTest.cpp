#include <gtest/gtest.h>
#include <Definitions.h>
#include <ASCIICharset.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class ASCIICharsetTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openCharset() {
        EXPECT_EQ(charset, nullptr);
        charset = new ASCIICharset();
        ASSERT_NE(charset, nullptr);
    }

    Charset* charset = nullptr;
};


TEST_P(ASCIICharsetTestFixture, EncodingTest)
{
    openCharset();

    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    HexVector actualResult = charset->encode(source);
    EXPECT_EQ(actualResult.toString(), expectedResult);
}


TEST_P(ASCIICharsetTestFixture, DecodingTest)
{
    openCharset();

    std::string source;
    std::string expectedResult;

    std::tie(expectedResult, source) = GetParam();
    HexVector hv = HexVector(source);

    std::string actualResult = charset->decode(hv);
    EXPECT_EQ(actualResult, expectedResult);
}

INSTANTIATE_TEST_CASE_P(
        ChrasetDecodingTestSuite,
        ASCIICharsetTestFixture,
        ::testing::Values(
                TestParamType("a", "61"),
                TestParamType("b", "62"),
                TestParamType("c", "63"),
                TestParamType("abc", "616263"),
                TestParamType("lmno", "6c6d6e6f"),
                TestParamType("efg", "656667"),
                TestParamType("abcdefghijklmnopqrstuvwxyz", "6162636465666768696a6b6c6d6e6f707172737475767778797a"),
                TestParamType("ABC", "414243"),
                TestParamType("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4142434445464748494a4b4c4d4e4f505152535455565758595a")
        ));