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
