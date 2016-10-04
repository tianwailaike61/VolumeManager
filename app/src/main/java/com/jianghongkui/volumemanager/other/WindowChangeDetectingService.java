package com.jianghongkui.volumemanager.other;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

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
//
//            ActivityInfo activityInfo = tryGetActivity(componentName);
//            boolean isActivity = activityInfo != null;
//            if (isActivity) {
//                currentActivityPackageName = activityInfo.packageName;
            currentActivityPackageName = event.getPackageName().toString();
            MLog.d(TAG, "currentActivityPackageName：" + currentActivityPackageName + "--lastActivityPackageName:" + lastActivityPackageName);
            if (!currentActivityPackageName.equals(lastActivityPackageName)) {
                maybeChangeVolume(currentActivityPackageName);
                lastActivityPackageName = currentActivityPackageName;
            }
//            }
        }
    }

    private void maybeChangeVolume(String packageName) {
        if (canChange()) {
            setVolume(getVolumeFromDatabases(packageName));
        }
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
            for (int i = 0; i < values.length; i++) {
                Utils.setVolume(context, i, values[i]);
            }
        } else {
            restore();
        }
    }

    private void restore() {
        String packageName = Application.PACKAGENAME;
        setVolume(getVolumeFromDatabases(packageName));
    }

    private boolean canChange() {
        int ringerMode = Utils.getRingerMode(getApplicationContext());
        return ringerMode == AudioManager.RINGER_MODE_NORMAL;
    }


    @Override
    public void onInterrupt() {

    }

    private class UserAdjustVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "onReceive:" + intent);
            if (intent.getAction().equals(ACTION_USER_ADJUST_VOLUME)) {
                Volume volume = new Volume();
                volume.setPackageName(currentActivityPackageName);
                //TODO  save the change
            }
        }
    }
}
