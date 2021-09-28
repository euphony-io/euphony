cmake_minimum_required(VERSION 3.4.1)

set(TEST_EUPHONY testEuphony)

# Include GoogleTest library
set (GOOGLETEST_ROOT ${ANDROID_NDK}/sources/third_party/googletest)
add_library (gtest STATIC ${GOOGLETEST_ROOT}/src/gtest_main.cc ${GOOGLETEST_ROOT}/src/gtest-all.cc)
target_include_directories (gtest PRIVATE ${GOOGLETEST_ROOT})
target_include_directories (gtest PUBLIC ${GOOGLETEST_ROOT}/include)

add_executable(
        ${TEST_EUPHONY}
        asciiCharsetTest.cpp
        base2Test.cpp
        base16Test.cpp
        defaultCharsetTest.cpp
        FFTTest.cpp
        FSKTest.cpp
        hexVectorTest.cpp
        packetTest.cpp
        packetBuilderTest.cpp
        packetErrorDetectorTest.cpp
        packetWithFSKTest.cpp
        waveTest.cpp
        waveBuilderTest.cpp
        waveRendererTest.cpp
)

target_link_libraries(${TEST_EUPHONY} PUBLIC euphony gtest)

set(TARGET_TEST_DIR /data/local/tmp/${TEST_EUPHONY}) # Directory on device to push tests.
set(TARGET_TEST_LIB_DIR ${TARGET_TEST_DIR}/${ANDROID_ABI})
set(LIBCPP_SHARED_PATH ${ANDROID_NDK}/sources/cxx-stl/llvm-libc++/libs/${ANDROID_ABI}/libc++_shared.so)

get_target_property( OBOE_LIBRARY_PATH oboe::oboe IMPORTED_LOCATION)
find_program(ADB NAMES adb PATHS ${ANDROID_SDK_ROOT}/platform-tools) # Verified to be working on Linux.

execute_process(COMMAND ${ADB} devices
        OUTPUT_VARIABLE ADB_DEVICE_STRING
        RESULT_VARIABLE ADB_DEVICE_STRING_RESULT)

string(REGEX REPLACE "[\t\n]" ";" ADB_DEVICE_LIST ${ADB_DEVICE_STRING})
list(FILTER ADB_DEVICE_LIST EXCLUDE REGEX ".*device.*")
list(REMOVE_ITEM ADB_DEVICE_LIST "")
list(LENGTH ADB_DEVICE_LIST ADB_DEVICE_LIST_LEN)

message(STATUS "ADB_DEVICE_LIST_LEN = ${ADB_DEVICE_LIST_LEN}")
foreach(SERIAL ${ADB_DEVICE_LIST})
    message(STATUS "ADB_DEVICE_SERIAL = ${SERIAL}")
    execute_process(COMMAND ${ADB} -s ${SERIAL} shell getprop ro.product.cpu.abi
            OUTPUT_VARIABLE ADB_DEVICE_ABI
            RESULT_VARIABLE ADB_DEVICE_ABI_RESULT)
    string(STRIP ${ADB_DEVICE_ABI} ADB_DEVICE_ABI)
    message(STATUS "ADB_DEVICE_ABI = ${ADB_DEVICE_ABI}")

    if(${ANDROID_ABI} STREQUAL ${ADB_DEVICE_ABI})
        # Prepare gtest for unit-test
        add_custom_command(TARGET ${TEST_EUPHONY} POST_BUILD
                COMMAND ${ADB} -s ${SERIAL} shell mkdir -p ${TARGET_TEST_LIB_DIR}

                # Push necessary libraries
                COMMAND ${ADB} -s ${SERIAL} push $<TARGET_FILE:euphony> ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} -s ${SERIAL} push ${OBOE_LIBRARY_PATH} ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} -s ${SERIAL} push ${LIBCPP_SHARED_PATH} ${TARGET_TEST_LIB_DIR}/

                # Push Euphony Test Binary
                COMMAND ${ADB} -s ${SERIAL} push $<TARGET_FILE:${TEST_EUPHONY}> ${TARGET_TEST_LIB_DIR}/
                COMMAND ${ADB} -s ${SERIAL} shell chmod 755 ${TARGET_TEST_LIB_DIR}/${TEST_EUPHONY}
                )

        # Run gtest & get the result.
        add_custom_command(TARGET ${TEST_EUPHONY} POST_BUILD
                COMMAND ${CMAKE_COMMAND} -DADB:STRING="${ADB}" -DSERIAL:STRING="${SERIAL}" -DTARGET_TEST_LIB_DIR:STRING="${TARGET_TEST_LIB_DIR}" -P ${CMAKE_CURRENT_SOURCE_DIR}/cmake.run.test.script
                )
    endif()
endforeach()


