#include <gtest/gtest.h>
#include <Definitions.h>
#include <wave/Wave.h>
#include <string>
#include <tuple>
#include <regex>

using std::string;
using namespace Euphony;

typedef std::tuple<int, int> TestParamType;

class WaveTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void makeFrequency() {
        EXPECT_EQ(wave, nullptr);
        wave = new Wave();
        ASSERT_NE(wave, nullptr);
    }

    Wave* wave = nullptr;
};

TEST_P(WaveTestFixture, WaveUnitTest)
{
    makeFrequency();

    int inputHz;
    int inputSize;

    std::tie(inputHz, inputSize) = GetParam();

    wave->setHz(inputHz);
    EXPECT_EQ(wave->getHz(), inputHz);
    wave->setSize(inputSize);
    EXPECT_EQ(wave->getSize(), inputSize);

    auto source = wave->getSource();
    EXPECT_EQ(source.capacity(), inputSize);
    EXPECT_EQ(source.size(), 0);

    EXPECT_EQ(wave->getSource().capacity(), inputSize);
    EXPECT_EQ(wave->getSource().size(), 0);

    wave->oscillate();
    auto source2 = wave->getSource();
    EXPECT_EQ(source2.capacity(), inputSize);
    EXPECT_EQ(source2.size(), inputSize);

    int zeroCount = 0;
    for(float floatSrc : source2) {
        if(floatSrc == 0)
            zeroCount++;
        else
            break;
    }
    EXPECT_EQ(zeroCount, 1); // because wave is sin

    EXPECT_EQ(wave->getInt16Source().capacity(), inputSize);
    EXPECT_EQ(wave->getInt16Source().size(), inputSize);

    std::vector<int16_t> int16Source = wave->getInt16Source();
    EXPECT_EQ(int16Source.capacity(), inputSize);
    EXPECT_EQ(int16Source.size(), inputSize);

    zeroCount = 0;
    for(short shortSrc : int16Source) {
        if(shortSrc == 0) {
            zeroCount++;
        }
        else
            break;
    }
    EXPECT_EQ(zeroCount, 1); // because wave is sin
    //int16_t data = wave->convertFloat2Int16(0.02);
    //EXPECT_EQ(data, 0);
}

INSTANTIATE_TEST_SUITE_P(
        WaveTest,
        WaveTestFixture,
        ::testing::Values(
                TestParamType(18000, 512),
                TestParamType(18000, 1024),
                TestParamType(18000, 2048),
                TestParamType(20000, 2048),
                TestParamType(21000, 2048)
));