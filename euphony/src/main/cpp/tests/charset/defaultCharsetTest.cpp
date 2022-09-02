#include <gtest/gtest.h>
#include <Definitions.h>
#include <charset/DefaultCharset.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::string> TestParamType;

class DefaultCharsetTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openCharset() {
        EXPECT_EQ(charset, nullptr);
        charset = new DefaultCharset();
        ASSERT_NE(charset, nullptr);
    }

    Charset* charset = nullptr;
};

TEST_P(DefaultCharsetTestFixture, DecodingTest)
{
    openCharset();

    std::string source;

    std::tie(source) = GetParam();

    std::string actualResult = charset->decode(source);
    EXPECT_EQ(actualResult, source);
}

INSTANTIATE_TEST_CASE_P(
        ChrasetDecodingTestSuite,
        DefaultCharsetTestFixture,
        ::testing::Values(
        TestParamType("61"),
        TestParamType("62"),
        TestParamType("63"),
        TestParamType("616263"),
        TestParamType("6c6d6e6f"),
        TestParamType("656667"),
        TestParamType("6162636465666768696a6b6c6d6e6f707172737475767778797a"),
        TestParamType("414243"),
        TestParamType("4142434445464748494a4b4c4d4e4f505152535455565758595a")
));