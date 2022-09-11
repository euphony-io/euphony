cmake_minimum_required(VERSION 3.4.1)

project(euphony
        VERSION 0.8)

# Legacy JNI
set(LEGACY_SRC
        arms/kiss_fft.c
        arms/kiss_fftr.c
        native-legacy.cpp
        )

find_library( log-lib log )
add_library( native-legacy SHARED ${LEGACY_SRC} )
target_compile_definitions(native-legacy PUBLIC FIXED_POINT=16)
target_link_libraries(
        native-legacy
        ${log-lib}
        -fopenmp
        -static-openmp
)

# Rearchitectured Euphony Native
set(EUPHONY_SRC
        arms/kiss_fft.c
        arms/kiss_fftr.c
        core/AudioStreamCallback.cpp
        core/charset/ASCIICharset.cpp
        core/modem/ASK.cpp
        core/charset/UTF8Charset.cpp
        core/charset/UTF16Charset.cpp
        core/charset/UTF32Charset.cpp
        core/charset/DefaultCharset.cpp
        core/base/Base2.cpp
        core/base/Base16.cpp
        core/base/Base32.cpp
        core/base/Base64.cpp
        core/base/Base16Exception.cpp
        core/base/Base32Exception.cpp
        core/base/Base64Exception.cpp
        core/renderer/EuPIOscillator.cpp
        core/renderer/EuPIRenderer.cpp
        core/fft/FFTHelper.cpp
        core/fft/FFTModel.cpp
        core/fft/FFTProcessor.cpp
        core/fft/BlueFFT.cpp
        core/modem/FSK.cpp
        core/HexVector.cpp
        core/packet/Packet.cpp
        core/packet/PacketBuilder.cpp
        core/packet/PacketErrorDetector.cpp
        core/TxEngine.cpp
        core/RxEngine.cpp
        core/wave/Wave.cpp
        core/wave/WaveBuilder.cpp
        core/renderer/WaveRenderer.cpp
        core/wakeup/WakeUpFFTSensor.cpp
        native-connector.cpp
        )

set(EUPHONY_HDR core)
set(DEBUG_HELPER debug-helper)
set(DEBUG_HELPER_SRC debug-helper/Trace.cpp)
set(SL_HEADERS arms/sl-headers)

include_directories( ${EUPHONY_HDR} ${SL_HEADERS} ${DEBUG_HELPER})

add_library(euphony SHARED ${EUPHONY_SRC} ${DEBUG_HELPER_SRC})
target_compile_definitions(euphony PUBLIC)

find_package(
        oboe REQUIRED CONFIG
)

target_link_libraries(
        euphony
        ${log-lib}
        OpenSLES
        oboe::oboe
        -fopenmp
        -static-openmp
)

# for c++ unit-test (gtest)
add_subdirectory(tests)