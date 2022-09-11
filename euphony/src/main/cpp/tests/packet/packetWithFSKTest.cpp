#include <gtest/gtest.h>
#include <Definitions.h>
#include <packet/Packet.h>
#include <modem/FSK.h>
#include <fft/FFTProcessor.h>
#include <tuple>
#include <charset/ASCIICharset.h>
#include <base/Base16.h>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class PacketWithFSKTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void createFSK() {
        EXPECT_EQ(fsk, nullptr);
        fsk = new FSK();
        ASSERT_NE(fsk, nullptr);
    }

    FSK* fsk = nullptr;
    Packet* pkt = nullptr;
};

TEST_P(PacketWithFSKTestFixture, PacketFSKTest)
{
    createFSK();

    string source;
    string expectedResult;

    std::tie(source, expectedResult) = GetParam();
    HexVector hv = ASCIICharset().encode(source);
    pkt = new Packet(hv);

    string actualResultCode = pkt->toString();
    EXPECT_EQ(actualResultCode, expectedResult);

    auto modulateResult = fsk->modulate(pkt->getPayloadStr());
    EXPECT_EQ(modulateResult.size(), source.size() * 2);

    auto demodulateResult = fsk->demodulate(modulateResult);

    EXPECT_EQ(expectedResult, demodulateResult->toString());
    pkt->clear();
}

INSTANTIATE_TEST_CASE_P(
        PacketFSKTestSuite,
        PacketWithFSKTestFixture,
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
