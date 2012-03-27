LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE	:= gltest-jni
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_CFLAGS	:= -Wall -O2 -fpic
LOCAL_SRC_FILES	:= cuberenderer.c cube.c
LOCAL_LDLIBS	:= -lGLESv1_CM
include $(BUILD_SHARED_LIBRARY)
