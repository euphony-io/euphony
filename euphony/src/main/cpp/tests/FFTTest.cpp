#include <gtest/gtest.h>
#include <Definitions.h>
#include <FSK.h>
#include <FFTProcessor.h>
#include <WaveBuilder.h>
#include <tuple>

using namespace Euphony;

typedef std::tuple<int, int, int, int> TestParamType;

class FFTTestFixture : public ::testing::TestWithParam<TestParamType> {

public:
    std::unique_ptr<FFTModel> fft = nullptr;
};

TEST_P(FFTTestFixture, FFTDefaultTest)
{
    int inputFrequency, inputFFTSize, inputSampleRate, expectedSpectrumIndex;
    std::tie(inputFrequency, inputFFTSize, inputSampleRate, expectedSpectrumIndex) = GetParam();

    fft = std::make_unique<FFTProcessor>(inputFFTSize, inputSampleRate);

    auto wave = Wave::create()
            .vibratesAt(inputFrequency)
            .setSize(2048)
            .build();

    auto shortWaveSourceVector = wave->getInt16Source();
    int16_t* shortWaveSource = &shortWaveSourceVector[0];
    float* resultBuf = fft->makeSpectrum(shortWaveSource);
    const int activeResult = FSK::getMaxIdxFromSource(resultBuf, 32, inputSampleRate, inputFFTSize);

    EXPECT_EQ(expectedSpectrumIndex, activeResult);
}

INSTANTIATE_TEST_SUITE_P(
        FFTTest,
        FFTTestFixture,
        ::testing::Values(
                /*
                 * Frequency, FFTSize, sampleRate, expectedSpectrumIndex
                 * kStartSignalFrequency = 17950
                 */
                TestParamType(17924, 512, 44100, -1),
                TestParamType(18000, 512, 44100, 0),
                TestParamType(18086, 512, 44100, 1),
                TestParamType(18172, 512, 44100, 2),
                TestParamType(18258, 512, 44100, 3),
                TestParamType(18345, 512, 44100, 4),
                TestParamType(18431, 512, 44100, 5),
                TestParamType(18517, 512, 44100, 6),
                TestParamType(18603, 512, 44100, 7),
                TestParamType(18689, 512, 44100, 8),
                TestParamType(18775, 512, 44100, 9),
                TestParamType(18861, 512, 44100, 10),
                TestParamType(18947, 512, 44100, 11),
                TestParamType(19033, 512, 44100, 12),
                TestParamType(19119, 512, 44100, 13),
                TestParamType(19205, 512, 44100, 14),
                TestParamType(19291, 512, 44100, 15),
                TestParamType(19377, 512, 44100, 16),
                TestParamType(19463, 512, 44100, 17),
                TestParamType(19549, 512, 44100, 18),
                TestParamType(19635, 512, 44100, 19),
                TestParamType(19721, 512, 44100, 20),
                TestParamType(19807, 512, 44100, 21),
                TestParamType(19893, 512, 44100, 22),
                TestParamType(19979, 512, 44100, 23),
                TestParamType(20065, 512, 44100, 24),
                TestParamType(20151, 512, 44100, 25),
                TestParamType(20237, 512, 44100, 26),
                TestParamType(20323, 512, 44100, 27),
                TestParamType(20409, 512, 44100, 28),
                TestParamType(20495, 512, 44100, 29),
                TestParamType(20581, 512, 44100, 30),
                TestParamType(20667, 512, 44100, 31),
                TestParamType(17956, 512, 44100, -1),
                TestParamType(17957, 512, 44100, -1),
                TestParamType(17958, 512, 44100, -1),
                TestParamType(17959, 512, 44100, 0),
                TestParamType(17960, 512, 44100, 0),
                TestParamType(18001, 512, 44100, 0),
                TestParamType(18043, 512, 44100, 0),
                TestParamType(18044, 512, 44100, 0),
                TestParamType(18045, 512, 44100, 1),
                TestParamType(18046, 512, 44100, 1),
                TestParamType(18087, 512, 44100, 1),
                TestParamType(18129, 512, 44100, 1),
                TestParamType(18130, 512, 44100, 1),
                TestParamType(18131, 512, 44100, 2),
                TestParamType(18132, 512, 44100, 2),
                TestParamType(18173, 512, 44100, 2),
                TestParamType(18215, 512, 44100, 2),
                TestParamType(18216, 512, 44100, 2),
                TestParamType(18217, 512, 44100, 3),
                TestParamType(18218, 512, 44100, 3),
                TestParamType(18259, 512, 44100, 3),
                TestParamType(18301, 512, 44100, 3),
                TestParamType(18302, 512, 44100, 3),
                TestParamType(18303, 512, 44100, 4),
                TestParamType(18304, 512, 44100, 4),
                TestParamType(18346, 512, 44100, 4),
                TestParamType(18324, 512, 44100, 4),
                TestParamType(18389, 512, 44100, 4),
                TestParamType(18390, 512, 44100, 5),
                TestParamType(18391, 512, 44100, 5),
                TestParamType(18432, 512, 44100, 5),
                TestParamType(18392, 512, 44100, 5),
                TestParamType(18475, 512, 44100, 5),
                TestParamType(18476, 512, 44100, 6),
                TestParamType(18477, 512, 44100, 6),
                TestParamType(18518, 512, 44100, 6),
                TestParamType(18560, 512, 44100, 6),
                TestParamType(18561, 512, 44100, 6),
                TestParamType(18562, 512, 44100, 7),
                TestParamType(18604, 512, 44100, 7),
                TestParamType(18647, 512, 44100, 7),
                TestParamType(18648, 512, 44100, 8),
                TestParamType(18690, 512, 44100, 8),
                TestParamType(18733, 512, 44100, 8),
                TestParamType(18734, 512, 44100, 9),
                TestParamType(18776, 512, 44100, 9),
                TestParamType(18819, 512, 44100, 9),
                TestParamType(18820, 512, 44100, 10),
                TestParamType(18863, 512, 44100, 10),
                TestParamType(18905, 512, 44100, 10),
                TestParamType(18906, 512, 44100, 11),
                TestParamType(18949, 512, 44100, 11),
                TestParamType(18991, 512, 44100, 11),
                TestParamType(18992, 512, 44100, 12),
                TestParamType(19035, 512, 44100, 12),
                TestParamType(19078, 512, 44100, 12),
                TestParamType(19079, 512, 44100, 13),
                TestParamType(19122, 512, 44100, 13),
                TestParamType(19164, 512, 44100, 13),
                TestParamType(19165, 512, 44100, 14),
                TestParamType(19208, 512, 44100, 14),
                TestParamType(19250, 512, 44100, 14),
                TestParamType(19251, 512, 44100, 15),
                TestParamType(19294, 512, 44100, 15),
                TestParamType(19336, 512, 44100, 15),
                TestParamType(19337, 512, 44100, 16)
        )
);