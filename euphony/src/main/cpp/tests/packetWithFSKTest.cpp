#include <gtest/gtest.h>
#include <Definitions.h>
#include <Packet.h>
#include <FSK.h>
#include <FFTProcessor.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<string, string> TestParamType;

class PacketWithFSKTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    void createFSK() {
        EXPECT_EQ(fsk, nullptr);
        fsk = new FSK();
        ASSERT_NE(fsk, nullptr);
    }

    void createFFT() {
        EXPECT_EQ(fft, nullptr);
        fft = new FFTProcessor(kFFTSize, kSampleRate);
        ASSERT_NE(fft, nullptr);
    }

    void allocatePacket() {
        EXPECT_EQ(pkt, nullptr);
        pkt = new Packet();
        ASSERT_NE(pkt, nullptr);
    }

    FSK* fsk = nullptr;
    FFTProcessor* fft = nullptr;
    Packet* pkt = nullptr;
};

TEST_P(PacketWithFSKTestFixture, PacketFSKTest)
{
    createFSK();
    createFFT();
    allocatePacket();

    string source;
    string expectedResult;

    std::tie(source, expectedResult) = GetParam();
    string actualResultCode = pkt->create(source);
    EXPECT_EQ(actualResultCode, expectedResult);

    auto fskResult = fsk->modulate(pkt->getStrPayload());
    EXPECT_EQ(fskResult.size(), source.size() * 2);
    pkt->clear();

    for (auto wave : fskResult) {
        auto vectorInt16Source = wave->getInt16Source();
        int16_t* int16Source = &vectorInt16Source[0];

        float* resultBuf = fft->makeSpectrum(int16Source);
        int resultBufSize = fft->getResultSize();

        int data = fsk->demodulate(resultBuf, resultBufSize);
        pkt->push(data);
    }

    EXPECT_EQ(expectedResult, pkt->toString());
    pkt->clear();
    fft->destroy();
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
