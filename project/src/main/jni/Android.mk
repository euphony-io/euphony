LOCAL_PATH := $(call my-dir)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/jni
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

include $(CLEAR_VARS)

LOCAL_MODULE := kissff
LOCAL_SRC_FILES := kiss_fft.c
LOCAL_LDLIBS := -llog
# LOCAL_ARM_MODE  := arm
LOCAL_CFLAGS    := -O2 -Wall -D__ANDROID__
LOCAL_CFLAGS    += -DFIXED_POINT

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := kissfftr
LOCAL_SRC_FILES := kiss_fftr.c
LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := kissff
# LOCAL_ARM_MODE  := arm
LOCAL_CFLAGS    := -O2 -Wall -D__ANDROID__
LOCAL_CFLAGS    += -DFIXED_POINT
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
# APP_STL := gnustl_static
# APP_CPPFLAGS += -frtti
# APP_CPPFLAGS += -fexceptions
# LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
# LOCAL_ARM_MODE  := arm
LOCAL_CFLAGS    := -O2 -Wall -D__ANDROID__
LOCAL_CFLAGS    += -DFIXED_POINT

LOCAL_MODULE := kissfft
#LOCAL_SRC_FILES := $(wildcard jni/*.c)
LOCAL_SRC_FILES := euphony_lib_receiver_KissFFT.cpp
#LOCAL_SRC_FILES := ca_uol_aig_fftpack_KissFFT.cpp \ kiss_fftr.c \ kiss_fft.c
LOCAL_LDLIBS := -llog
LOCAL_STATIC_LIBRARIES := kissfftr

include $(BUILD_SHARED_LIBRARY)








