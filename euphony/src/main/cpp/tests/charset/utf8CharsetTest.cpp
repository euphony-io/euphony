#include <Definitions.h>
#include <charset/UTF8Charset.h>
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
                TestParamType("안녕하세요", "ec9588eb8595ed9598ec84b8ec9a94"),
                TestParamType("서울특별시", "ec849cec9ab8ed8ab9ebb384ec8b9c"),
                TestParamType("연남동", "ec97b0eb82a8eb8f99"),
                TestParamType("유포니", "ec9ca0ed8faceb8b88"),
                TestParamType("Euphony 유포니", "457570686f6e7920ec9ca0ed8faceb8b88"),
                TestParamType("UTF8 유닛테스트 집단지성으로 모으기!", "5554463820ec9ca0eb8b9bed858cec8aa4ed8ab820eca791eb8ba8eca780ec84b1ec9cbceba19c20ebaaa8ec9cbceab8b021"),
                TestParamType("미니 프레첼 볶음양념맛", "ebafb8eb8b8820ed9484eba088ecb2bc20ebb3b6ec9d8cec9691eb8590eba79b"),
                TestParamType("OpensourceContributionAcademy", "4f70656e736f75726365436f6e747269627574696f6e41636164656d79"),
                TestParamType("euphony", "657570686f6e79"),
                TestParamType("encoder/decoder", "656e636f6465722f6465636f646572"),
                TestParamType("JavascriptFormatter", "4a617661736372697074466f726d6174746572"),
                TestParamType("Signals and Systems", "5369676e616c7320616e642053797374656d73"),
                TestParamType("t-distribution", "742d646973747269627574696f6e"),
                TestParamType("Nyquist-Shannon sampling theorem", "4e7971756973742d5368616e6e6f6e2073616d706c696e67207468656f72656d"),
                TestParamType("https://en.wikipedia.org/wiki/Sampling_(signal_processing)#Sampling_rate", "68747470733a2f2f656e2e77696b6970656469612e6f72672f77696b692f53616d706c696e675f287369676e616c5f70726f63657373696e67292353616d706c696e675f72617465"),
                TestParamType("https://github.com/euphony-io/euphony/discussions/93", "68747470733a2f2f6769746875622e636f6d2f657570686f6e792d696f2f657570686f6e792f64697363757373696f6e732f3933"),
                TestParamType("utf8 to hexadecimal converter world's simplest utf8 tool", "7574663820746f2068657861646563696d616c20636f6e76657274657220776f726c6427732073696d706c657374207574663820746f6f6c"),
                TestParamType("010-1234-5678", "3031302d313233342d35363738"),
                TestParamType("36.5℃", "33362e35e28483"),
                TestParamType("@XYZ", "4058595a"),
                TestParamType(".com", "2e636f6d"),
                TestParamType("ભુભુગ", "e0aaade0ab81e0aaade0ab81e0aa97"),
                TestParamType("大廈千間 夜臥八尺", "e5a4a7e5bb88e58d83e9969320e5a49ce887a5e585abe5b0ba"),
                TestParamType("Teşekkür ederim", "5465c59f656b6bc3bc722065646572696d"),
                TestParamType("Юу хийж байна?", "d0aed18320d185d0b8d0b9d0b620d0b1d0b0d0b9d0bdd0b03f"),
                TestParamType("Anh có gì vui không?", "416e682063c3b32067c3ac20767569206b68c3b46e673f"),
                TestParamType("vétéran", "76c3a974c3a972616e"),
                TestParamType("Ankara''ya giden uçak biletini rezervasyon yapmak istiyorum.", "416e6b6172612727796120676964656e2075c3a7616b2062696c6574696e692072657a6572766173796f6e207961706d616b2069737469796f72756d2e"),
                TestParamType("أهلا", "d8a3d987d984d8a7"),
                TestParamType("韓國", "e99f93e59c8b"),
                TestParamType("こんにちは", "e38193e38293e381abe381a1e381af"),
                TestParamType("ការស្រាវជ្រាវរបស់សាមសុង", "e19e80e19eb6e19e9ae19e9fe19f92e19e9ae19eb6e19e9ce19e87e19f92e19e9ae19eb6e19e9ce19e9ae19e94e19e9fe19f8be19e9fe19eb6e19e98e19e9fe19ebbe19e84"),
                TestParamType("7 Οκτωβρίου 2021",  "3720ce9fcebacf84cf89ceb2cf81ceafcebfcf852032303231"),
                TestParamType("હું ભૂખ્યો છું", "e0aab9e0ab81e0aa8220e0aaade0ab82e0aa96e0ab8de0aaafe0ab8b20e0aa9be0ab81e0aa82"),
                TestParamType("તમને મળીને આનંદ થયો",  "e0aaa4e0aaaee0aaa8e0ab8720e0aaaee0aab3e0ab80e0aaa8e0ab8720e0aa86e0aaa8e0aa82e0aaa620e0aaa5e0aaafe0ab8b"),
                TestParamType("Quiero comer pollo.",  "51756965726f20636f6d657220706f6c6c6f2e")
        ));