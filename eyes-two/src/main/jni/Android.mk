# Set this to the absolute path of the tesseract-android-tools project.
#TESSERACT_TOOLS_PATH := $(call my-dir)/../../../../tess-two

LOCAL_PATH := $(call my-dir)

EXPLODED_AAR := $(LOCAL_PATH)/../../../build/intermediates/exploded-aar
TESS_TWO := $(EXPLODED_AAR)/com.rmtheis/tess-two
TESS_TWO_V := 8.0.0
TESS_TWO_LIBS := $(TESS_TWO)/$(TESS_TWO_V)/jni

PREBUILD_LIBS := $(TESS_TWO_LIBS)
PREBUILT_PATH := $(PREBUILD_LIBS)/$(TARGET_ARCH_ABI)

# Do not modify anything below this line.

#PREBUILT_PATH := $(TESSERACT_TOOLS_PATH)/libs/$(TARGET_ARCH_ABI)

include $(call all-subdir-makefiles)
