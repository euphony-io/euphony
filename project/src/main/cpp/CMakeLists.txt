# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

set(LEGACY_SRC
        kiss_fft.c
        kiss_fftr.c
        native-legacy.cpp
        )

set(EUPHONY_SRC
        kiss_fft.c
        kiss_fftr.c
        EuphonyRxCore.cpp
        EpnyFrequency.cpp
        EpnyOscillator.cpp
        EpnySoundGenerator.cpp
        EpnyTxEngine.cpp
        EpnyAudioStreamCallback.cpp
        native-connector.cpp
        )

set(DEBUG_HELPER ../debug-helper)
set(DEBUG_HELPER_SRC ../debug-helper/Trace.cpp)
set(SL_HEADERS ../headers)

include_directories(${SL_HEADERS} ${DEBUG_HELPER})

# add_library ( <name> [STATIC|SHARED|MODULE] <src> <src> )
add_library( native-legacy SHARED ${LEGACY_SRC} )
add_library( euphony SHARED ${EUPHONY_SRC} ${DEBUG_HELPER_SRC})

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