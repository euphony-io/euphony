#ifndef EUPHONY_DEFINITIONS_H
#define EUPHONY_DEFINITIONS_H

#include <cstdint>
#include <math.h>
#include "Wave.h"

namespace Euphony {
    typedef std::vector<std::shared_ptr<Wave>> WaveList;

    constexpr int32_t kChannelCount = 2;
    constexpr int32_t kSampleRate = 44100;
    constexpr int32_t kFFTSize = 512;
    constexpr int32_t kBufferSize = 2048;
    constexpr int32_t kBufferFadeLength = 256;
    constexpr int32_t kFrequencyInterval = kSampleRate / kFFTSize;
    constexpr int32_t kStandardFrequency = 18001;
    constexpr int32_t kStartSignalFrequency = kStandardFrequency - kFrequencyInterval;
    constexpr double kPi = M_PI;
    constexpr double kTwoPi = kPi * 2.0;

    static constexpr const char* BASE16_EXCEPTION_MSG = "BASE16 couldn't support this code value";

    enum class ModeType : int32_t {
        DEFAULT = 0, // Default Soundless Communication
        DETECT = 1, // For Wave Detection
        EUPI = 2, // For EUPI Mode
        /*
         * TODO: DISTANCE CALCULATION (Distance = Speed * Time)
        DISTANCE = 4
         */
    };

    enum class ModulationType : int32_t {
        FSK = 0,
        /*
        TODO: v0.7.1.6 had ASK feature. but v0.8 has to create it.
        ASK,
         */
        /*
        TODO: Rearchitecturing necessary because the CPFSK modulation type has some glitch sound.
        CPFSK
         */
    };

    enum class BaseType : int32_t {
        BASE2 = 0,
        BASE16 = 1,
    };

    enum class CharsetType : int32_t {
        ASCII = 0,
        /* TODO: Develop Charset for UTF8 */
        UTF8 = 1
    };
}

#endif