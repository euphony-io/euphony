#include <Definitions.h>
#include <UTF16Charset.h>
#include <gtest/gtest.h>

#include <tuple>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class UTF16CharsetTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openCharset() {
        EXPECT_EQ(charset, nullptr);
        charset = new UTF16Charset();
        ASSERT_NE(charset, nullptr);
    }

    Charset *charset = nullptr;
};

TEST_P(UTF16CharsetTestFixture, EncodingTest) {
    openCharset();

    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    HexVector actualResult = charset->encode(source);
    EXPECT_EQ(actualResult.toString(), expectedResult);
}

TEST_P(UTF16CharsetTestFixture, DecodingTest) {
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
        UTF16CharsetTestFixture,
        ::testing::Values(
                TestParamType("가", "ac00"),
                TestParamType("나", "b098"),
                TestParamType("홍길동", "d64dae38b3d9"),
                TestParamType("a", "0061"),
                TestParamType("b", "0062"),
                TestParamType("@XYZ", "004000580059005a"),
                TestParamType(".com", "002e0063006f006d"),
                TestParamType("서울특별시", "c11cc6b8d2b9bcc4c2dc"),
                TestParamType("010-1234-5678", "003000310030002d0031003200330034002d0035003600370038"),
                TestParamType("36.5℃", "00330036002e00352103")
        ));