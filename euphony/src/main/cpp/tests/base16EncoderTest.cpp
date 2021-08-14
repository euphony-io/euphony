#include <gtest/gtest.h>
#include <Definitions.h>
#include <Base16.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class Base16EncoderTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openEncoder() {
        EXPECT_EQ(codec, nullptr);
        codec = new Base16();
        ASSERT_NE(codec, nullptr);
    }

    BaseCodec* codec = nullptr;
};

TEST_P(Base16EncoderTestFixture, ASCIIEncodingTest)
{
    openEncoder();

    string source;
    string expectedEncodedResult;

    std::tie(source, expectedEncodedResult) = GetParam();

    string actualResult = codec->encode(source);
    EXPECT_EQ(actualResult, expectedEncodedResult);
}

INSTANTIATE_TEST_CASE_P(
        AsciiEncodingTestSuite,
        Base16EncoderTestFixture,
        ::testing::Values(
                TestParamType("a", "61"),
                TestParamType("b", "62"),
                TestParamType("c", "63"),
                TestParamType("abc", "616263"),
                TestParamType("lmno", "6c6d6e6f"),
                TestParamType("efg", "656667"),
                TestParamType("abcdefghijklmnopqrstuvwxyz", "6162636465666768696a6b6c6d6e6f707172737475767778797a"),
                TestParamType("ABC", "414243"),
                TestParamType("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "4142434445464748494a4b4c4d4e4f505152535455565758595a"),
                TestParamType("hell", "68656c6c"),
                TestParamType("hello, euphony", "68656c6c6f2c20657570686f6e79")
                ));

