cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror -Wall -std=c++14")

# Include GoogleTest library
set (GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)
add_library (gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
target_include_directories (gtest PRIVATE ${GOOGLETEST_ROOT})
target_include_directories (gtest PUBLIC ${GOOGLETEST_ROOT}/include)

message(STATUS "ndk path = ${ANDROID_NDK}")
message(STATUS "googletest root = ${GOOGLETEST_ROOT}")

# Include Euphony sources
set (EUPHONY_DIR ${CMAKE_CURRENT_SOURCE_DIR}/..)
include_directories(
        ${EUPHONY_DIR}/core
        ${EUPHONY_DIR}/core/source
)

add_executable(
        testEuphony
        encoderTest.cpp
)

target_link_libraries(testEuphony gtest euphony)