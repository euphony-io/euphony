#include <gtest/gtest.h>
#include <Definitions.h>
#include <PacketErrorDetector.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class PacketErrorDetectorTestFixture : public ::testing::TestWithParam<TestParamType> {
};

TEST_P(PacketErrorDetectorTestFixture, PacketErrorDetectorTest)
{
    string source;
    string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    string actualResult = PacketErrorDetector::makeParityAndChecksum(source);
    EXPECT_EQ(expectedResult, actualResult);
}

TEST_F(PacketErrorDetectorTestFixture, ChecksumTest)
{
    vector<int> source {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
    EXPECT_EQ(PacketErrorDetector::makeChecksum(source), 14);
    EXPECT_EQ(PacketErrorDetector::verifyChecksum(source, 14), true);
    EXPECT_EQ(PacketErrorDetector::verifyChecksum(source, 13), false);
}

TEST_F(PacketErrorDetectorTestFixture, ParityCodeTest)
{
    vector<int> source {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};
    EXPECT_EQ(PacketErrorDetector::makeParallelParity(source), 4);
    EXPECT_EQ(PacketErrorDetector::verifyParallelParity(source, 4), true);
    EXPECT_EQ(PacketErrorDetector::verifyParallelParity(source, 5), false);
}

INSTANTIATE_TEST_CASE_P(
        PacketErrorDetectorTestSuite,
        PacketErrorDetectorTestFixture,
        ::testing::Values(
        TestParamType("61", "97"),
        TestParamType("62", "84"),
        TestParamType("63", "75"),
        TestParamType("616263", "86"),
        TestParamType("6c6d6e6f", "20"),
        TestParamType("656667", "c2"),
        TestParamType("6162636465666768696a6b6c6d6e6f707172737475767778797a", "aa"),
        TestParamType("414243", "e4"),
        TestParamType("4142434445464748494a4b4c4d4e4f505152535455565758595a", "ea")
));
