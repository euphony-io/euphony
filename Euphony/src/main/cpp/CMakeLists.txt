cmake_minimum_required(VERSION 3.4.1)

project(Euphony
        VERSION 0.8)

set(LEGACY_SRC
        arms/kiss_fft.c
        arms/kiss_fftr.c
        native-legacy.cpp
        )

set(EUPHONY_SRC
        arms/kiss_fft.c
        arms/kiss_fftr.c
        core/source/Frequency.cpp
        core/source/Oscillator.cpp
        core/source/SoundGenerator.cpp
        core/source/FFTProcessor.cpp
        core/source/TxEngine.cpp
        core/source/RxEngine.cpp
        core/source/AudioStreamCallback.cpp
        native-connector.cpp
        )

set(EUPHONY_HDR core)
set(DEBUG_HELPER debug-helper)
set(DEBUG_HELPER_SRC debug-helper/Trace.cpp)
set(SL_HEADERS headers)

include_directories( ${EUPHONY_HDR} ${SL_HEADERS} ${DEBUG_HELPER})

# add_library ( <name> [STATIC|SHARED|MODULE] <src> <src> )
add_library( native-legacy SHARED ${LEGACY_SRC} )
target_compile_definitions(native-legacy PUBLIC FIXED_POINT=16)
add_library(euphony SHARED ${EUPHONY_SRC} ${DEBUG_HELPER_SRC})
target_compile_definitions(euphony PUBLIC FIXED_POINT=16)

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

find_package(
        oboe REQUIRED CONFIG
)

target_link_libraries(
        euphony
        ${log-lib}
        oboe::oboe
)