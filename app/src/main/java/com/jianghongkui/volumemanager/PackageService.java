package com.jianghongkui.volumemanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.IBinder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PackageService extends Service {
    List<PackageInfo> packageInfos;
    ActivityManager am;
    UsageStatsManager usm;

    private String lastPackageName;

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            String currentPackageName = getCurrentPackageName();
            if (currentPackageName != null && !currentPackageName.equals(lastPackageName)) {
                //TODO...
                lastPackageName = currentPackageName;
            }
        }
    };

    private String getCurrentPackageName() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (am == null) {
                am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            }
            List<ActivityManager.RunningTaskInfo> appTasks = am.getRunningTasks(1);
            if (null != appTasks && !appTasks.isEmpty()) {
                return appTasks.get(0).topActivity.getPackageName();
            }
        } else {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 10000;
            if (usm == null) {
                usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usm.queryEvents(beginTime, endTime);
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.getPackageName();
                }
            }
            if (!android.text.TextUtils.isEmpty(result)) {
                return result;
            }
        }
        return "";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        SharedPreferences sp = getSharedPreferences("firstload", Activity.MODE_PRIVATE);
        if (sp.getBoolean("firstload", true)) {
            packageInfos = getPackageManager().getInstalledPackages(0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("firstload", false);
            editor.commit();
        }
        Timer timer = new Timer();
        timer.schedule(timerTask, 10000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
