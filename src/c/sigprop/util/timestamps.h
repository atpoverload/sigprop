#include <jni.h>

#ifndef _Included_sigprop_util_Timestamps
#define _Included_sigprop_util_Timestamps

JNIEXPORT jlong JNICALL Java_sigprop_util_Timestamps_epochTimeNative
   (JNIEnv *, jclass);

JNIEXPORT jlong JNICALL Java_sigprop_util_Timestamps_monotonicTimeNative
   (JNIEnv *, jclass);
