#include <gtest/gtest.h>
#include <Definitions.h>
#include <Base16.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class Base16DecoderTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openDecoder() {
        EXPECT_EQ(codec, nullptr);
        codec = new Base16();
        ASSERT_NE(codec, nullptr);
    }

    BaseCodec* codec = nullptr;
};

TEST_P(Base16DecoderTestFixture, ASCIIDecodingTest)
{
openDecoder();

string source;
string expectedDecodedResult;

std::tie(expectedDecodedResult, source) = GetParam();

string actualResult = codec->decode(source);
EXPECT_EQ(actualResult, expectedDecodedResult);
}

INSTANTIATE_TEST_CASE_P(
        AsciiDecodingTestSuite,
        Base16DecoderTestFixture,
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