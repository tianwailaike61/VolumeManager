package com.jianghongkui.volumemanager.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.WelcomeActivity;
import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jianghongkui on 2016/10/9.
 */

public class VolumeChangeService extends Service {
    private final static String TAG = "VolumeChangeService";
    public final static String ACTION_ACTIVITY_CHANGED = "android.action.activity_changed";

    public static final String ACTION_NOTIFICATION_STATE_CHANGED = "android.action.notification_state_changed";
    public static final String ACTION_NOTIFICATION_MASSAFE_CHANGED = "android.action.notification_message_changed";

    private Notification notification;

    private final int NOTIFICATION_FLAG = 0;

    private NotificationReceiver notificationReceiver;
    private ActivityChangedReceiver activityChangedReceiver;

    private VolumeDBManager manager;

    private String currentActivityPackageName = Application.PACKAGENAME;
    private String lastActivityPackageName;

    private Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        activityChangedReceiver = new ActivityChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_ACTIVITY_CHANGED);
        registerReceiver(activityChangedReceiver, intentFilter);


        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NOTIFICATION_STATE_CHANGED);
        filter.addAction(ACTION_NOTIFICATION_MASSAFE_CHANGED);
        registerReceiver(notificationReceiver, filter);

        if (Settings.showNotification) {
            startNotification();
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    private void startNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, WelcomeActivity.class), 0);
        notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("notification is started")
                .setContentTitle("Notification Title")
                .setContentText("This is the notification message")
                .setContentIntent(pendingIntent)
                .build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manager.notify(NOTIFICATION_FLAG, notification);
    }


    private void stopNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_FLAG);
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_FLAG, notification);
    }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_NOTIFICATION_STATE_CHANGED.equals(action)) {
                boolean showNotification = intent.getBooleanExtra("state", true);
                if (showNotification) {
                    startNotification();
                } else {
                    stopNotification();
                }
            } else if (ACTION_NOTIFICATION_MASSAFE_CHANGED.equals(action) && Settings.showNotification) {
                if (notification == null)
                    stopNotification();
                notification.tickerText = intent.getStringExtra("message");
                updateNotification();
            }
        }
    }

    public class ActivityChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ACTIVITY_CHANGED)) {
                String currentActivityPackageName = intent.getStringExtra("PackageName");
                if (!Settings.list.contains(currentActivityPackageName) && !currentActivityPackageName.equals(lastActivityPackageName)) {
                    maybeChangeVolume(currentActivityPackageName);
                    lastActivityPackageName = currentActivityPackageName;
                }
            }
        }
    }
}


