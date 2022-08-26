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
        core/source/AudioStreamCallback.cpp
        core/source/ASCIICharset.cpp
        core/source/ASK.cpp
        core/source/UTF8Charset.cpp
        core/source/UTF16Charset.cpp
        core/source/UTF32Charset.cpp
        core/source/DefaultCharset.cpp
        core/source/Base2.cpp
        core/source/Base16.cpp
        core/source/Base32.cpp
        core/source/Base64.cpp
        core/source/Base16Exception.cpp
        core/source/Base32Exception.cpp
        core/source/Base64Exception.cpp
        core/source/EuPIOscillator.cpp
        core/source/EuPIRenderer.cpp
        core/source/FFTHelper.cpp
        core/source/FFTModel.cpp
        core/source/FFTProcessor.cpp
        core/source/BlueFFT.cpp
        core/source/FSK.cpp
        core/source/HexVector.cpp
        core/source/Packet.cpp
        core/source/PacketBuilder.cpp
        core/source/PacketErrorDetector.cpp
        core/source/TxEngine.cpp
        core/source/RxEngine.cpp
        core/source/Wave.cpp
        core/source/WaveBuilder.cpp
        core/source/WaveRenderer.cpp
        core/source/WakeUpFFTSensor.cpp
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