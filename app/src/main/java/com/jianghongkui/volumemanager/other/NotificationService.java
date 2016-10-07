package com.jianghongkui.volumemanager.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.SettingsActivity;
import com.jianghongkui.volumemanager.WelcomeActivity;
import com.jianghongkui.volumemanager.util.MLog;

public class NotificationService extends Service {

    private final static String TAG = "NotificationService";

    public static final String ACTION_NOTIFICATION_CHANGED = "android.action.notification_changed";

    private Notification notification;

    private final int NOTIFICATION_FLAG = 0;
    private NotificationReceiver notificationReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NOTIFICATION_CHANGED);
        registerReceiver(notificationReceiver, filter);
        startNotification();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (notificationReceiver != null)
            unregisterReceiver(notificationReceiver);
        stopNotification();
        super.onDestroy();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MLog.d(TAG, "NotificationReceiver.onReceive:" + action);
            if (ACTION_NOTIFICATION_CHANGED.equals(action)) {
                if (notification == null)
                    stopNotification();
                notification.tickerText = intent.getStringExtra("message");
                updateNotification();
            }
        }


    }
}
