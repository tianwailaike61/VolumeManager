package com.jianghongkui.volumemanager.other;

import android.annotation.TargetApi;
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
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.WelcomeActivity;
import com.jianghongkui.volumemanager.model.Notice;
import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jianghongkui on 2016/10/9.
 */

public class VolumeChangeService extends Service {
    private final static String TAG = "VolumeChangeService";
    public final static String ACTION_ACTIVITY_CHANGED = "android.action.activity_changed";
    public static final String ACTION_NOTIFICATION_STATE_CHANGED = "android.action.notification_state_changed";
    public static final String ACTION_NOTIFICATION_MASSAFE_CHANGED = "android.action.notification_message_changed";
    public static final String ACTION_NOTIFICATION_NO_VOLUME = "android.action.notification_no_volume";

    // private Notification notification;

    private final int NOTIFICATION_FLAG = 100;

    private NotificationReceiver notificationReceiver;
    private ActivityChangedReceiver activityChangedReceiver;

    private VolumeDBManager manager;

    private boolean isNoVolume = false;

    private String currentActivityPackageName = Application.PACKAGENAME;
    private String lastActivityPackageName;

    private Context context;

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            updateNotification();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                MLog.d(TAG, "release lock");
//                lock.unlock();
//            }
//        }
//    };


    @Override
    public void onCreate() {
        super.onCreate();
        activityChangedReceiver = new ActivityChangedReceiver();
        IntentFilter intentFilter1 = new IntentFilter(ACTION_ACTIVITY_CHANGED);
        registerReceiver(activityChangedReceiver, intentFilter1);

        notificationReceiver = new NotificationReceiver();
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(ACTION_NOTIFICATION_STATE_CHANGED);
        intentFilter3.addAction(ACTION_NOTIFICATION_MASSAFE_CHANGED);
        intentFilter3.addAction(ACTION_NOTIFICATION_NO_VOLUME);
        registerReceiver(notificationReceiver, intentFilter3);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();

        Settings.init(context);


        if (Settings.showNotification) {
            startNotification();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(activityChangedReceiver);
        unregisterReceiver(notificationReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void maybeChangeVolume(String packageName) {
        if (!packageName.equals(Application.PACKAGENAME)) {
            Volume volume = getNextVolume(packageName);
            if (canChange() && volumeEquals(volume) && !Settings.list.contains(packageName)) {
                setVolume(getVolumeFromDatabases(packageName));
            }
        }
    }

    private Volume getNextVolume(String packageName) {
        Volume nextVolume = getVolumeFromDatabases(packageName);
        if (nextVolume == null || nextVolume.isFollowSystem())
            nextVolume = getVolumeFromDatabases(Application.SYSTEM);
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
        MLog.d(TAG, "set volume:" + volume);
        if (volume != null) {
            int[] values = volume.getValues();
            int[] currentvalues = Utils.getCurrentVolume(context);
            ArrayList<Integer> notChangeVolumeTypes = new ArrayList<>(2);
            MLog.d(TAG, "setVolume-isChangeMusicWhenPlaying:" + isChangeMusicWhenPlaying());
            if (!isChangeMusicWhenPlaying()) {
                values[3] = currentvalues[3];
                notChangeVolumeTypes.add(new Integer(3));
            }
            MLog.d(TAG, "setVolume-isChangeVoiceWhenPlaying:" + isChangeVoiceWhenPlaying());
            if (!isChangeVoiceWhenPlaying()) {
                values[0] = currentvalues[0];
                notChangeVolumeTypes.add(new Integer(0));
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(Notice.KEY, getNotice(notChangeVolumeTypes));
//            bundle.putString("Name", Utils.getAppName(context, currentActivityPackageName));
//            bundle.putIntegerArrayList("NotChangeVolumeTypes", notChangeVolumeTypes);
            for (int i = 0; i < values.length; i++) {
                Utils.setVolume(context, i, values[i], bundle);
            }
        } else {
            restore();
        }
    }

    private Notice getNotice(ArrayList<Integer> integers) {
        Notice notice = new Notice();
        notice.setType(Notice.CHANGE_VOLUME);
        notice.setName(Utils.getAppName(context, currentActivityPackageName));
        notice.setIntegers(integers);
        return notice;
    }

    private void restore() {
        String packageName = Application.SYSTEM;
        setVolume(getVolumeFromDatabases(packageName));
    }

    private boolean canChange() {
        int ringerMode = Utils.getRingerMode(getApplicationContext());
        if (ringerMode == AudioManager.RINGER_MODE_SILENT || isNoVolume) {
            return false;
        }
        return true;
    }

    private boolean isChangeMusicWhenPlaying() {
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioManager.isMusicActive()) {
            return Settings.forceChangeMusic;
        } else {
            return true;
        }
    }

    private boolean isChangeVoiceWhenPlaying() {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Service.TELEPHONY_SERVICE);
        if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
            return true;
        }
        return Settings.forceChangeVioceCall;
    }


    private boolean volumeEquals(Volume volume) {
        MLog.d(TAG, "volumeEquals-" + volume);
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


    public void startNotification() {
        startForeground(NOTIFICATION_FLAG, getNotification(null, null));
//        NotificationManager manager = (NotificationManager) getSystemService(
//                Context.NOTIFICATION_SERVICE);
//        manager.notify(NOTIFICATION_FLAG, getNotification(null, null));
    }

    private Notification getNotification(String msg, Bitmap bitmap) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_notification);
        remoteViews.setImageViewResource(R.id.icon_iv, R.mipmap.ic_launcher);
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.close_volume, bitmap);
        } else {
            remoteViews.setImageViewBitmap(R.id.close_volume, getImageView());
        }
        remoteViews.setTextViewText(R.id.notification_state, context.getString(R.string.notification_title));
        remoteViews.setTextViewText(R.id.notification_msg, "");
        if (!TextUtils.isEmpty(msg))
            remoteViews.setTextViewText(R.id.notification_msg, msg);
        setClickIntent(remoteViews);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!TextUtils.isEmpty(msg)) {
            notification.tickerText = msg;
        }

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
            notification.bigContentView = remoteViews;
        }
        notification.contentView = remoteViews;
        return notification;
    }

    private void setClickIntent(RemoteViews remoteViews) {
        // int requestCode = (int) SystemClock.uptimeMillis();
        Intent intent = new Intent(ACTION_NOTIFICATION_NO_VOLUME);
        //intent.setClass(context, VolumeChangeService.class);
        //PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.close_volume, pendingIntent);
    }

//    private void startNotification() {
//        NotificationManager manager = (NotificationManager) getSystemService(
//                Context.NOTIFICATION_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, WelcomeActivity.class), 0);
//        notification = new Notification.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("notification is started")
//                .setContentTitle("Notification Title")
//                .setContentText("This is the notification message")
//                .setContentIntent(pendingIntent)
//                .build();
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        manager.notify(NOTIFICATION_FLAG, notification);
//    }

    private Bitmap getImageView() {
        int resourceId = R.drawable.volume_full;
        MLog.d(TAG, "getImageView isNoVolume:" + isNoVolume);
        if (isNoVolume)
            resourceId = R.drawable.volume_muted;
        Bitmap bitmap = Utils.drawableToBitamp(getResources().getDrawable(resourceId), 70, 70);
        return bitmap;
    }

    private void stopNotification() {
        stopForeground(true);
    }

    private void setVolumeModel(boolean ismuted) {
        Settings.isMutedModel = ismuted;
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        for (int i = 1; i < 6; i++) {
            audioManager.setStreamMute(i, ismuted);
        }
    }

    private class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            MLog.d(TAG, "NotificationReceiver onReceive:" + intent);
            String action = intent.getAction();
            if (ACTION_NOTIFICATION_STATE_CHANGED.equals(action)) {
                boolean showNotification = intent.getBooleanExtra("state", true);
                if (showNotification) {
                    startNotification();
                } else {
                    stopNotification();
                }
            } else if (ACTION_NOTIFICATION_MASSAFE_CHANGED.equals(action) && Settings.showNotification) {
                synchronized (this) {
                    String msg = intent.getStringExtra("Message");
                    startForeground(NOTIFICATION_FLAG, getNotification(msg, null));
                }
            } else if (ACTION_NOTIFICATION_NO_VOLUME.equals(action)) {
                String str = intent.getStringExtra("userchange");
                if (str == null) {
                    isNoVolume = !isNoVolume;
                } else {
                    isNoVolume = intent.getBooleanExtra("isNoVolume", false);
                }
                String msg = isNoVolume ? getString(R.string.notification_no_volume) : null;
                setVolumeModel(isNoVolume);
                startForeground(NOTIFICATION_FLAG, getNotification(msg, getImageView()));
            }
        }
    }

    public class ActivityChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ACTIVITY_CHANGED)) {
                currentActivityPackageName = intent.getStringExtra("PackageName");
                MLog.d(TAG, "currentActivityPackageName-" + currentActivityPackageName);
                if (!currentActivityPackageName.equals(lastActivityPackageName)) {
                    maybeChangeVolume(currentActivityPackageName);
                    lastActivityPackageName = currentActivityPackageName;
                }
            }
        }
    }
}


