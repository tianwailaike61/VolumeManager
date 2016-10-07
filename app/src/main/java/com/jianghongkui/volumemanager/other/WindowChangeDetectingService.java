package com.jianghongkui.volumemanager.other;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jianghongkui on 2016/9/21.
 */

public class WindowChangeDetectingService extends AccessibilityService {

    private final String TAG = "WindowChangeDetectingService";

    public static final String Service = Application.PACKAGENAME + "/"
            + WindowChangeDetectingService.class.getPackage().getName() + "." + WindowChangeDetectingService.class.getSimpleName();
    private final String ACTION_USER_ADJUST_VOLUME = "com.action.user_adjust_volume";
    private final String ACTION_APP_SET_VOLUME = "com.action.app_set_volume";
    private final String USER_CHANGE_VOLUME = "android.media.VOLUME_CHANGED_ACTION";

    private String currentActivityPackageName;
    private String lastActivityPackageName;

    private VolumeDBManager manager;

    private Context context;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        initReceiver();

        context = getApplicationContext();

        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    private void initReceiver() {
        VolumeChangeReciver volumeChangeReciver = new VolumeChangeReciver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(USER_CHANGE_VOLUME);
        filter1.addAction(ACTION_APP_SET_VOLUME);
        registerReceiver(volumeChangeReciver, filter1);

        UserAdjustVolumeReceiver userAdjustVolumeReceiver = new UserAdjustVolumeReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ACTION_USER_ADJUST_VOLUME);
        registerReceiver(userAdjustVolumeReceiver, filter2);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            ComponentName componentName = new ComponentName(
//                    event.getPackageName().toString(),
//                    event.getClassName().toString()
//            );
            currentActivityPackageName = event.getPackageName().toString();
            MLog.d(TAG, "currentActivityPackageName：" + currentActivityPackageName + "--lastActivityPackageName:" + lastActivityPackageName);
            if (!Settings.list.contains(currentActivityPackageName) && !currentActivityPackageName.equals(lastActivityPackageName)) {
                maybeChangeVolume(currentActivityPackageName);
                lastActivityPackageName = currentActivityPackageName;
                //currentProgramName=
            }
        }
    }

    private void maybeChangeVolume(String packageName) {
        Volume volume = getNextVolume(packageName);
        if (canChange() && shouldChange(volume)) {
            setVolume(getVolumeFromDatabases(packageName));
        }
    }

    private Volume getNextVolume(String packageName) {
        Volume nextVolume = getVolumeFromDatabases(packageName);
        if (nextVolume == null || nextVolume.isFollowSystem())
            nextVolume = getVolumeFromDatabases(Application.PACKAGENAME);
        return nextVolume;
    }

    @Nullable
    private Volume getVolumeFromDatabases(String packageName) {
        if (manager == null)
            manager = VolumeDBManager.newInstace(context);
        List<Volume> volumes = manager.query(packageName);
        if (volumes != null && volumes.size() != 0)
            return volumes.get(0);
        return null;
    }

    private void setVolume(Volume volume) {
        MLog.e(TAG, "set volume:" + volume);
        if (volume != null) {
            int[] values = volume.getValues();
            Bundle bundle = new Bundle();
            bundle.putString("Name", getAppName(currentActivityPackageName));
            for (int i = 0; i < values.length; i++) {
                Utils.setVolume(context, i, values[i], bundle);
            }
        } else {
            restore();
        }
    }

    private String getAppName(String packageName) {
        PackageManager packageManager = null;
        PackageInfo info = null;
        try {
            packageManager = getPackageManager();
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            packageManager = null;
            info = null;
        }
    }

    private void restore() {
        String packageName = Application.PACKAGENAME;
        setVolume(getVolumeFromDatabases(packageName));
    }

    private boolean canChange() {
        int ringerMode = Utils.getRingerMode(getApplicationContext());
        return ringerMode != AudioManager.RINGER_MODE_SILENT;
    }

    private boolean shouldChange(Volume volume) {
        int[] currentValues = Utils.getCurrentVolume(context);
        if (Settings.saveUserChanges) {
            Volume lastVolume = getVolumeFromDatabases(lastActivityPackageName);
            if ((lastVolume == null || lastVolume.isFollowSystem()) && volume.isFollowSystem())
                return false;
        }
        if (Arrays.equals(volume.getValues(), currentValues)) {
            return false;
        }
        return true;
    }


    @Override
    public void onInterrupt() {

    }

    private class UserAdjustVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "onReceive:" + intent);
            if (intent.getAction().equals(ACTION_USER_ADJUST_VOLUME)) {
                if (Settings.saveUserChanges) {
                    if (Settings.isSaveIntoSystem) {
                        saveIntoSystem();
                    } else {
                        saveIntoPrograme();
                    }

                }
            }
        }

        private void saveVolumeIntoDatabases(Volume volume) {
            if (manager == null)
                manager = VolumeDBManager.newInstace(context);
            List<Volume> volumes = manager.query(volume.getPackageName());
            if (volumes != null && volumes.size() != 0)
                manager.update(volume);
            else
                manager.insert(volume);
        }

        private void saveIntoSystem() {
            Volume systemVolume = getVolumeFromDatabases(Application.PACKAGENAME);
            systemVolume.setValues(Utils.getCurrentVolume(context));
            saveVolumeIntoDatabases(systemVolume);
        }

        private void saveIntoPrograme() {
            Volume volume = new Volume();
            volume.setFollowSystem(false);
            volume.setPackageName(currentActivityPackageName);
            volume.setValues(Utils.getCurrentVolume(context));
            saveVolumeIntoDatabases(volume);
        }
    }
}
