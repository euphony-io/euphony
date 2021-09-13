#include <gtest/gtest.h>
#include <Definitions.h>
#include <PacketErrorDetector.h>
#include <string>
#include <vector>
#include <tuple>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class PacketErrorDetectorTestFixture : public ::testing::TestWithParam<TestParamType> {
};

TEST_P(PacketErrorDetectorTestFixture, PacketErrorDetectorTest)
{
    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    std::string actualResult = PacketErrorDetector::makeParityAndChecksum(source);
    EXPECT_EQ(expectedResult, actualResult);
}

TEST_F(PacketErrorDetectorTestFixture, ErrorCodeTest)
{

    std::vector<u_int8_t> source {0x6, 0x1};
    std::string expectedResult = "97";
    HexVector hv = HexVector(source);
    EXPECT_EQ(expectedResult, PacketErrorDetector::makeParityAndChecksum(hv.getHexSource()));

    std::vector<u_int8_t> source2 {0x61, 0x62, 0x63};
    std::string expectedResult2 = "86";
    HexVector hv2 = HexVector(source2);
    EXPECT_EQ(expectedResult2, PacketErrorDetector::makeParityAndChecksum(hv2.getHexSource()));
}


TEST_F(PacketErrorDetectorTestFixture, ChecksumTest)
{
    std::vector<u_int8_t> source {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};

    HexVector hv = HexVector(source);
    HexVector actualResult = HexVector(1);
    actualResult.pushBack(14);
    EXPECT_EQ(PacketErrorDetector::makeChecksum(hv).toString(), actualResult.toString());
    EXPECT_EQ(PacketErrorDetector::verifyChecksum(hv, 14), true);
    EXPECT_EQ(PacketErrorDetector::verifyChecksum(hv, 13), false);
}

TEST_F(PacketErrorDetectorTestFixture, ParityCodeTest)
{
    std::vector<u_int8_t> source {0x6, 0x8, 0x6, 0x5, 0x6, 0xc, 0x6, 0xc, 0x6, 0xf};

    HexVector hv = HexVector(source);
    HexVector actualResult = HexVector(1);
    actualResult.pushBack(4);
    EXPECT_EQ(PacketErrorDetector::makeParallelParity(hv).toString(), actualResult.toString());
    EXPECT_EQ(PacketErrorDetector::verifyParallelParity(hv, 4), true);
    EXPECT_EQ(PacketErrorDetector::verifyParallelParity(hv, 5), false);
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
