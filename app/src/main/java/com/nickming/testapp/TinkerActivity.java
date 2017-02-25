package com.nickming.testapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nickming.testapp.tinker.ResultService;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

public class TinkerActivity extends AppCompatActivity {

    private TextView mBugTxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker2);
        startService(new Intent(this, ResultService.class));
        mBugTxv= (TextView) findViewById(R.id.bugTxvId);
        mBugTxv.setText("存在bug!");
        mBugTxv.setText("热修复热修复热修复hot fix!");
    }

    public void loadPatch(View view)
    {
        TinkerInstaller.onReceiveUpgradePatch(getApplication(),
                Environment.getExternalStorageDirectory().getAbsolutePath()+"/patch_signed_7zip.apk");
    }
}
