#ifndef EUPHONY_DEFINITIONS_H
#define EUPHONY_DEFINITIONS_H

#include <cstdint>
#include <math.h>
#include "Wave.h"

namespace Euphony {
    typedef std::vector<std::shared_ptr<Wave>> WaveList;

    constexpr int32_t kChannelCount = 1;
    constexpr int32_t kSampleRate = 44100;
    constexpr int32_t kFFTSize = 512;
    constexpr int32_t kBufferSize = 2048;
    constexpr int32_t kBufferFadeLength = 128;
    constexpr int32_t kFrequencyInterval = kSampleRate / kFFTSize;
    constexpr int32_t kStandardFrequency = 18001;
    constexpr int32_t kStartSignalFrequency = kStandardFrequency - kFrequencyInterval;
    constexpr double kPi = M_PI;
    constexpr double kTwoPi = kPi * 2.0;

    static constexpr const char* BASE16_EXCEPTION_MSG = "BASE16 couldn't support this code value";
    static constexpr const char* BASE32_EXCEPTION_MSG = "BASE32 couldn't support this code value";
    static constexpr const char* BASE64_EXCEPTION_MSG = "BASE64 couldn't support this code value";

    enum class Result : int32_t {
        OK = 0,
        ERROR_GENERAL = 1,
        ERROR_ALREADY_RUNNING = 2
    };

    enum class Status : int32_t {
        RUNNING = 0,
        STOP = 1
    };

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
        ASK = 1,

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