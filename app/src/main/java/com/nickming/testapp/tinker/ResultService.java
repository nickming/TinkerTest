package com.nickming.testapp.tinker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.TinkerServiceInternals;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 2017/2/25.
 */

public class ResultService extends DefaultTinkerResultService {

    private static final String TAG = "ResultService";


    @Override
    public void onPatchResult(final PatchResult result) {
        if (result == null) {
            TinkerLog.e(TAG, "SampleResultService received null result!!!!");
            return;
        }
        TinkerLog.i(TAG, "SampleResultService receive result: %s", result.toString());

        //first, we want to kill the recover process
        TinkerServiceInternals.killTinkerPatchServiceProcess(getApplicationContext());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (result.isSuccess) {
                    Toast.makeText(getApplicationContext(), "patch success, please restart process", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "patch fail, please check reason", Toast.LENGTH_LONG).show();
                }
            }
        });
        // is success and newPatch, it is nice to delete the raw file, and restart at once
        // for old patch, you can't delete the patch file
        if (result.isSuccess) {
            File rawFile = new File(result.rawPatchFilePath);
            if (rawFile.exists()) {
                TinkerLog.i(TAG, "save delete raw patch file");
                SharePatchFileUtil.safeDeleteFile(rawFile);
            }
            //not like TinkerResultService, I want to restart just when I am at background!
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            if (checkIfNeedKill(result)) {
                if (isBackground()) { //后台了再更新
                    TinkerLog.i(TAG, "it is in background, just restart process");
                    restartProcess();
                } else {
                    //we can wait process at background, such as onAppBackground
                    //or we can restart when the screen off
                    TinkerLog.i(TAG, "tinker wait screen to restart process");
                    new ScreenState(getApplicationContext(), new ScreenState.IOnScreenOff() {
                        @Override
                        public void onScreenOff() {
                            restartProcess();
                        }
                    });
                }
            } else {
                TinkerLog.i(TAG, "I have already install the newly patch version!");
            }
        }

        //repair current patch fail, just clean!
        if (!result.isSuccess) {
            //if you have not install tinker this moment, you can use TinkerApplicationHelper api
            Tinker.with(getApplicationContext()).cleanPatch();
        }
    }

    /**
     * you can restart your process through service or broadcast
     */
    private void restartProcess() {
        TinkerLog.i(TAG, "app is background now, i can kill quietly");
        //you can send service or broadcast intent to restart your process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    static class ScreenState {
        interface IOnScreenOff {
            void onScreenOff();
        }

        ScreenState(Context context, final IOnScreenOff onScreenOffInterface) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent in) {
                    String action = in == null ? "" : in.getAction();
                    TinkerLog.i(TAG, "ScreenReceiver action [%s] ", action);
                    if (Intent.ACTION_SCREEN_OFF.equals(action)) {

                        context.unregisterReceiver(this);

                        if (onScreenOffInterface != null) {
                            onScreenOffInterface.onScreenOff();
                        }
                    }
                }
            }, filter);
        }
    }


    public boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getApplication().getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
