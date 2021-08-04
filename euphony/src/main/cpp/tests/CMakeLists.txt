cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Werror -Wall -std=c++14")

# Include GoogleTest library
set (GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)
add_library (gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
target_include_directories (gtest PRIVATE ${GOOGLETEST_ROOT})
target_include_directories (gtest PUBLIC ${GOOGLETEST_ROOT}/include)

add_executable(
        testEuphony
        encoderTest.cpp
)

target_link_libraries(testEuphony PUBLIC euphony gtest)

set(TARGET_TEST_DIR /data/local/tmp/testEuphony) # Directory on device to push tests.
set(TARGET_TEST_LIB_DIR ${TARGET_TEST_DIR}/${ANDROID_ABI})
set(LIBCPP_SHARED_PATH ${ANDROID_NDK}/sources/cxx-stl/llvm-libc++/libs/${ANDROID_ABI}/libc++_shared.so)

get_target_property( OBOE_LIBRARY_PATH oboe::oboe IMPORTED_LOCATION)
find_program(ADB NAMES adb PATHS ${ANDROID_SDK_ROOT}/platform-tools) # Verified to be working on Linux.

execute_process(COMMAND ${ADB} shell getprop ro.product.cpu.abi OUTPUT_VARIABLE ADB_DEVICE_ABI RESULT_VARIABLE ADB_DEVICE_ABI_RESULT)

if( ${ADB_DEVICE_ABI_RESULT} )
    message("NO ABI OR MORE THAN ONE DEVICE/EMULATOR")
else()
    string(STRIP ${ADB_DEVICE_ABI} ADB_DEVICE_ABI)
    if(${ANDROID_ABI} STREQUAL ${ADB_DEVICE_ABI})
        # Prepare gtest for unit-test
        add_custom_command(TARGET testEuphony POST_BUILD
                COMMAND ${ADB} shell mkdir -p ${TARGET_TEST_LIB_DIR}

                # Push necessary libraries
                COMMAND ${ADB} push $<TARGET_FILE:euphony> ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} push ${OBOE_LIBRARY_PATH} ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} push ${LIBCPP_SHARED_PATH} ${TARGET_TEST_LIB_DIR}/

                # Push Euphony Test Binary
                COMMAND ${ADB} push $<TARGET_FILE:testEuphony> ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} shell chmod 755 ${TARGET_TEST_LIB_DIR}/testEuphony
                )

        # Run gtest & get the result.
        execute_process(COMMAND ${ADB} shell LD_LIBRARY_PATH=${TARGET_TEST_LIB_DIR} ${TARGET_TEST_LIB_DIR}/testEuphony OUTPUT_VARIABLE GTEST_OUTPUT RESULT_VARIABLE GTEST_RESULT )

        if( ${GTEST_RESULT} )
            message(FATAL_ERROR "** Gtest Failure **\n${GTEST_OUTPUT}")
        else()
            message("**** Gtest Success ****")
        endif()
    endif()
endif()