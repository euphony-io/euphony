#include <gtest/gtest.h>
#include <Definitions.h>
#include <tuple>
#include <charset/ASCIICharset.h>
#include <charset/DefaultCharset.h>
#include <charset/UTF8Charset.h>
#include <charset/UTF16Charset.h>
#include <charset/UTF32Charset.h>
#include <iostream>
#include <algorithm>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class CharsetAutoSelectorTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void openCharset() {
        EXPECT_EQ(asciiCharset, nullptr);
        asciiCharset = new ASCIICharset();
        ASSERT_NE(asciiCharset, nullptr);

        EXPECT_EQ(defaultCharset, nullptr);
        defaultCharset = new DefaultCharset();
        ASSERT_NE(defaultCharset, nullptr);

        EXPECT_EQ(utf8Charset, nullptr);
        utf8Charset = new UTF8Charset();
        ASSERT_NE(utf8Charset, nullptr);

        EXPECT_EQ(utf16Charset, nullptr);
        utf16Charset = new UTF16Charset();
        ASSERT_NE(utf16Charset, nullptr);

        EXPECT_EQ(utf32Charset, nullptr);
        utf32Charset = new UTF32Charset();
        ASSERT_NE(utf32Charset, nullptr);
    }

    Charset *asciiCharset = nullptr;
    Charset *defaultCharset = nullptr;
    Charset *utf8Charset = nullptr;
    Charset *utf16Charset = nullptr;
    Charset *utf32Charset = nullptr;
};

TEST_P(CharsetAutoSelectorTestFixture, SelectTest) {
    openCharset();

    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    HexVector asciiResult = asciiCharset->encode(source);
    HexVector defaultResult = defaultCharset->encode(source);
    HexVector utf8Result = utf8Charset->encode(source);
    HexVector utf16Result = utf16Charset->encode(source);
    HexVector utf32Result = utf32Charset->encode(source);

    HexVector results[] = {asciiResult, defaultResult, utf8Result, utf16Result, utf32Result};

    std::sort(results, results+5, [](HexVector& left, HexVector& right) {
        return left.getSize() < right.getSize();
    });
    
    HexVector actualResult = results[0];

    EXPECT_EQ(actualResult.toString(), expectedResult);
}

INSTANTIATE_TEST_CASE_P(
        CharsetAutoSelectorTestSuite,
        CharsetAutoSelectorTestFixture,
        ::testing::Values(
                TestParamType("a", "61"),
                TestParamType("b", "62"),
                TestParamType("가", "eab080"),
                TestParamType("나", "eb8298")
));