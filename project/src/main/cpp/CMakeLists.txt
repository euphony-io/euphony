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
        EuphonyRx.cpp
        EuphonyTx.cpp
        )

# add_library ( <name> [STATIC|SHARED|MODULE] <src> <src> )
add_library( native-legacy SHARED ${LEGACY_SRC} )
add_library( euphony SHARED ${EUPHONY_SRC} )

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

find_package(
        oboe REQUIRED CONFIG
)

target_link_libraries(
        native-legacy
        ${log-lib}
        oboe::oboe
        euphony
)