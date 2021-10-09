#include <Definitions.h>
#include <UTF8Charset.h>
#include <gtest/gtest.h>

#include <tuple>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class UTF8CharsetTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openCharset() {
        EXPECT_EQ(charset, nullptr);
        charset = new UTF8Charset();
        ASSERT_NE(charset, nullptr);
    }

    Charset *charset = nullptr;
};

TEST_P(UTF8CharsetTestFixture, EncodingTest) {
    openCharset();

    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    HexVector actualResult = charset->encode(source);
    EXPECT_EQ(actualResult.toString(), expectedResult);
}

TEST_P(UTF8CharsetTestFixture, DecodingTest) {
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
        UTF8CharsetTestFixture,
        ::testing::Values(
                TestParamType("a", "61"),
                TestParamType("b", "62"),
                TestParamType("가", "eab080"),
                TestParamType("각", "eab081"),
                TestParamType("나", "eb8298"),
                TestParamType("홍길동", "ed998deab8b8eb8f99"),
                TestParamType("@XYZ", "4058595a"),
                TestParamType(".com", "2e636f6d"),
                TestParamType("서울특별시", "ec849cec9ab8ed8ab9ebb384ec8b9c"),
                TestParamType("010-1234-5678", "3031302d313233342d35363738"),
                TestParamType("36.5℃", "33362e35e28483")
        ));