package com.nickming.testapp.jni;

/**
 * Created by admin on 17-2-24.
 */

public class TestJniUtils {

    static {
        System.loadLibrary("testJni");
    }

    public native static String getStrFromJni();

    public native static void  deliverStrToJni(String str);
}
