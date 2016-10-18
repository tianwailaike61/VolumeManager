package com.jianghongkui.volumemanager.other;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;

import com.jianghongkui.volumemanager.model.Notice;
import com.jianghongkui.volumemanager.model.Settings;
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
//    private final String ACTION_USER_ADJUST_VOLUME = "com.action.user_adjust_volume";
//    private final String ACTION_APP_SET_VOLUME = "com.action.app_set_volume";
//    private final String USER_CHANGE_VOLUME = "android.media.VOLUME_CHANGED_ACTION";

    private String currentActivityPackageName;
    private String lastActivityPackageName;
    public String currentChangeVolumePackageName;
    private Context context;
    private VolumeDBManager manager;

    private VolumeChangeReciver volumeChangeReciver;
    private UserAdjustVolumeReceiver userAdjustVolumeReceiver;
    private TimeTickReceiver timeTickReceiver;

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
        volumeChangeReciver = new VolumeChangeReciver();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(VolumeChangeReciver.ACTION_USER_CHANGE_VOLUME);
        filter1.addAction(VolumeChangeReciver.ACTION_APP_CHANGE_VOLUME);
        registerReceiver(volumeChangeReciver, filter1);

        userAdjustVolumeReceiver = new UserAdjustVolumeReceiver();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(UserAdjustVolumeReceiver.ACTION_USER_ADJUST_VOLUME);
        registerReceiver(userAdjustVolumeReceiver, filter2);

        timeTickReceiver = new TimeTickReceiver();
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(volumeChangeReciver);
        unregisterReceiver(userAdjustVolumeReceiver);
        unregisterReceiver(timeTickReceiver);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            ComponentName componentName = new ComponentName(
//                    event.getPackageName().toString(),
//                    event.getClassName().toString()
//            );
            currentActivityPackageName = event.getPackageName().toString();
            MLog.d(TAG, "onAccessibilityEvent currentActivityPackageName:" + currentActivityPackageName);
            if (!currentActivityPackageName.equals(lastActivityPackageName) && !Settings.list.contains(currentActivityPackageName)) {
                Intent intent = new Intent(VolumeChangeService.ACTION_ACTIVITY_CHANGED);
                intent.putExtra("PackageName", currentActivityPackageName);
                sendBroadcast(intent);
                lastActivityPackageName = currentActivityPackageName;
                currentChangeVolumePackageName = null;
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void saveIntoSystem() {
        Volume systemVolume = getVolumeFromDatabases(Application.SYSTEM);
        systemVolume.setValues(Utils.getCurrentVolume(context));
        saveVolumeIntoDatabases(systemVolume);

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

    private void saveIntoPrograme() {
        Volume volume = new Volume();
        volume.setFollowSystem(false);
        volume.setPackageName(lastActivityPackageName);
        MLog.d(TAG, "saveIntoPrograme--" + lastActivityPackageName);
        volume.setValues(Utils.getCurrentVolume(context));
        saveVolumeIntoDatabases(volume);
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

    public class UserAdjustVolumeReceiver extends BroadcastReceiver {
        public static final String ACTION_USER_ADJUST_VOLUME = "com.action.user_adjust_volume";

        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "onReceive:" + intent);
            MLog.d(TAG, "onReceive:" + currentActivityPackageName + " " + lastActivityPackageName);
            String action = intent.getAction();
            if (ACTION_USER_ADJUST_VOLUME.equals(action)) {
                if (Settings.saveUserChanges && shouldSave()) {
                    if (Settings.isSaveIntoSystem) {
                        saveIntoSystem();
                    } else {
                        saveIntoPrograme();
                    }
                    if (!lastActivityPackageName.equals(currentChangeVolumePackageName)) {
                        notifyMessage();
                        currentChangeVolumePackageName = lastActivityPackageName;
                    }

                }
            }
        }

        private boolean shouldSave() {
            if (lastActivityPackageName.equals(Application.PACKAGENAME))
                return false;
            if (Settings.list.contains(lastActivityPackageName))
                return false;
            return true;
        }

        private void notifyMessage() {
            MLog.d(TAG, "notifyMessage");
            Intent newIntent = new Intent(MessageNotifyReceiver.ACTION_MESSAGE_NOTIFY);
            Notice notice = new Notice();
            notice.setType(Notice.SAVE_VOLUME);
            if (Settings.isSaveIntoSystem) {
                notice.setName("system");
            } else {
                notice.setName(lastActivityPackageName);
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(Notice.KEY, notice);
            newIntent.putExtras(bundle);
            context.sendBroadcast(newIntent);
        }
    }

    private class TimeTickReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Settings.allowSelfStart && !Utils.isServiceWork(context, VolumeChangeReciver.class.getSimpleName())) {
                Intent intent1 = new Intent(context, VolumeChangeService.class);
                context.startService(intent1);
            }
        }
    }
}
