cmake_minimum_required(VERSION 3.4.1)

project(euphony
        VERSION 0.8)

#Legacy JNI
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

# for euphony
set(EUPHONY_SRC
        arms/kiss_fft.c
        arms/kiss_fftr.c
        core/source/Base16.cpp
        core/source/FFTModel.cpp
        core/source/Frequency.cpp
        core/source/Oscillator.cpp
        core/source/WaveGenerator.cpp
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
add_library(euphony SHARED ${EUPHONY_SRC} ${DEBUG_HELPER_SRC})
target_compile_definitions(euphony PUBLIC FIXED_POINT=16)

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
# TODO: unit-test CMakeLists is not completed
add_subdirectory(tests)