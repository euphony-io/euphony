#include <gtest/gtest.h>
#include <Definitions.h>
#include <packet/PacketBuilder.h>
#include <tuple>
#include <charset/ASCIICharset.h>

using namespace Euphony;

typedef std::tuple<std::string, std::string> TestParamType;

class PacketBuilderTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::shared_ptr<Packet> pkt = nullptr;
};

TEST_P(PacketBuilderTestFixture, CreationForASCIIAndBase16TestByPacketBuilder)
{
    std::string source;
    std::string expectedResult;

    std::tie(source, expectedResult) = GetParam();

    pkt = Packet::create()
            .setPayloadWithASCII(source)
            .basedOnBase16()
            .build();

    // Check total result
    EXPECT_EQ(pkt->toString(), expectedResult);
}

TEST_F(PacketBuilderTestFixture, CreationForASCIIAndBase2TestByPacketBuilder)
{
    std::string source = "a";
    std::string expectedResult = "S0110000110010111";
    pkt = Packet::create()
            .setPayloadWithASCII(source)
            .basedOnBase2()
            .build();
    // Check total result
    EXPECT_EQ(pkt->toString(), expectedResult);

    pkt->clear();
    source = "abc";
    expectedResult = "S01100001011000100110001110000110";
    pkt = Packet::create()
            .setPayloadWithASCII(source)
            .basedOnBase2()
            .build();

    EXPECT_EQ(pkt->toString(), expectedResult);
}

INSTANTIATE_TEST_SUITE_P(
        PacketBuilderTest,
        PacketBuilderTestFixture,
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
        )
);
