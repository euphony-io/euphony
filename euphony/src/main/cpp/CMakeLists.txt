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
        core/source/Wave.cpp
        core/source/WaveBuilder.cpp
        )

set(EUPHONY_HDR core)
set(DEBUG_HELPER debug-helper)
set(DEBUG_HELPER_SRC debug-helper/Trace.cpp)

include_directories( ${EUPHONY_HDR} ${DEBUG_HELPER})

add_library(euphony SHARED ${EUPHONY_SRC} ${DEBUG_HELPER_SRC})
target_compile_definitions(euphony PUBLIC FIXED_POINT=16)

target_link_libraries(
        euphony
        ${log-lib}
        -fopenmp
        -static-openmp
)

# for c++ unit-test (gtest)
add_subdirectory(tests)