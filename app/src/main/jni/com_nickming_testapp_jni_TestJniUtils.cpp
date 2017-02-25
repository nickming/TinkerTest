//
// Created by admin on 17-2-24.
//
#include <stdio.h>
#include <stdlib.h>
#include "com_nickming_testapp_jni_TestJniUtils.h"

/*
 * Class:     com_nickming_testapp_jni_TestJniUtils
 * Method:    getStrFromJni
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_nickming_testapp_jni_TestJniUtils_getStrFromJni
  (JNIEnv *env, jclass jobj)
  {
        return env->NewStringUTF((char *)"Hello from JNI !");
  }

/*
 * Class:     com_nickming_testapp_jni_TestJniUtils
 * Method:    deliverStrToJni
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_nickming_testapp_jni_TestJniUtils_deliverStrToJni
  (JNIEnv *env, jclass jobj, jstring str)
  {

  }
