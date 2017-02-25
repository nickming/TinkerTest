package com.nickming.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nickming.testapp.jni.TestJniUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.LOG_DEBUG)
            Log.i(TAG, "onCreate: " + TestJniUtils.getStrFromJni());
        Log.i(TAG, "onCreate: " + BuildConfig.SERVER_URL);
    }
}
