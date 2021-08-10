#ifndef EUPHONY_DEFINITIONS_H
#define EUPHONY_DEFINITIONS_H

#include <cstdint>

namespace Euphony {
    constexpr int32_t kSampleRate = 44100;
    constexpr int32_t kFFTSize = 512;
    constexpr int32_t kBufferSize = 2048;
    constexpr int32_t kBufferFadeLength = 256;
    constexpr int32_t kFrequencyInterval = kSampleRate / kFFTSize;
    constexpr int32_t kStandardFrequency = 18017;
    constexpr int32_t kStartSignalFrequency = kStandardFrequency - kFrequencyInterval;

    enum class ModeType : int32_t {
        DEFAULT = 0, // Default Soundless Communication
        LIVE = 1, // Live Souldless Communication
        DETECT = 2, // For Wave Detection
        COMMAND = 3, //
        FIND = 4
    };

    enum class ModulationType : int32_t {
        ASK = 0,
        FSK = 1,
        CPFSK = 2
    };

    enum class EncodingType : int32_t {
        BASE16 = 0,
        BASE32 = 1,
        BASE64 = 2
    };

    enum class CharacterSet : int32_t {
        ASCII = 0,
        UTF8 = 1
    };
}

#endif