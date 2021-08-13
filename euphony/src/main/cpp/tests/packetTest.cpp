#include <gtest/gtest.h>
#include <Definitions.h>
#include <Packet.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class PacketTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void allocatePacket() {
        EXPECT_EQ(pkt, nullptr);
        pkt = new Packet();
        ASSERT_NE(pkt, nullptr);
    }

    Packet* pkt = nullptr;
};

TEST_P(PacketTestFixture, PacketCreationTest)
{
    allocatePacket();

    string source;
    string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    string actualResult = pkt->create(source);
    EXPECT_EQ(actualResult, expectedResult);
}

INSTANTIATE_TEST_CASE_P(
        PacketTestSuite,
        PacketTestFixture,
        ::testing::Values(
                TestParamType("a", "S6197"),
                TestParamType("b", "S6284"),
                TestParamType("c", "S6375"),
                TestParamType("abc", "S61626386"),
                TestParamType("lmno", "S6c6d6e6f20"),
                TestParamType("efg", "S656667c2"),
                TestParamType("abcdefghijklmnopqrstuvwxyz", "S6162636465666768696a6b6c6d6e6f707172737475767778797aaa"),
                TestParamType("ABC", "S414243e4"),
                TestParamType("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "S4142434445464748494a4b4c4d4e4f505152535455565758595aea")
        ));