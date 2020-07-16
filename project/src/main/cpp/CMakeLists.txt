# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)

set(SOURCE_FILES
        kiss_fft.c
        kiss_fftr.c
        native-legacy.cpp
        )
add_library(
        # Sets the name of the library.
        native-legacy
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${SOURCE_FILES}
)

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
)