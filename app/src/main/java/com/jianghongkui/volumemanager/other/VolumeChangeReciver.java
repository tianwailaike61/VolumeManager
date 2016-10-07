package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jianghongkui on 2016/9/21.
 */

public class VolumeChangeReciver extends BroadcastReceiver {
    private final String TAG = "VolumeChangeReciver";
    private boolean flag = true;
    private final String APP_CHANGE_VOLUME = "com.action.app_set_volume";
    private final String USER_CHANGE_VOLUME = "android.media.VOLUME_CHANGED_ACTION";
    private final String ACTION_USER_ADJUST_VOLUME = "com.action.user_adjust_volume";

    private Context context;

    private String name;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        //MLog.d(TAG, "onReceive:" + intent);
        if (APP_CHANGE_VOLUME.equals(action)) {
            flag = false;
            String appName = intent.getStringExtra("Name");
            if (appName != null && !appName.equals(name)) {
                showMessage(appName + " is open,app help to change the volume!");
                name = appName;
            }
        }
        if (USER_CHANGE_VOLUME.equals(action)) {
            if (flag) {
                // MLog.d(TAG, "user change the volume");
                if (Settings.saveUserChanges) {
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_USER_ADJUST_VOLUME);
                    context.sendBroadcast(intent1);
                }
            } else {
                //MLog.d(TAG, "this app change the volume");
                flag = true;
            }
        }
//        if (USER_CHANGE_VOLUME_MODE.equals(action)) {
//            final int ringerMode = Utils.getRingerMode(context);
//            switch (ringerMode) {
//                case AudioManager.RINGER_MODE_NORMAL:
//                    MLog.e("RINGER_MODE_NORMAL");
//                    break;
//                case AudioManager.RINGER_MODE_VIBRATE:
//                    MLog.e("RINGER_MODE_VIBRATE");
//                    break;
//                case AudioManager.RINGER_MODE_SILENT:
//                    MLog.e("RINGER_MODE_SILENT");
//                    break;
//            }
//        }
    }

    private void showMessage(String msg) {
        if (Settings.showNotification) {
            Intent intent = new Intent(NotificationService.ACTION_NOTIFICATION_CHANGED);
            intent.putExtra("message", msg);
            context.sendBroadcast(intent);
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

//    /**
//     *if the database has volume about this packageName
//     *if the result is true , get the volume
//     *if the result is false , changed to the system volume
//     * @param packageName
//     */
//    private void changeVolume(String packageName) {
//        if (manager == null)
//            manager = VolumeDBManager.newInstace(context);
//        Volume volume = null;
//        ArrayList<Volume> volumes = (ArrayList<Volume>) manager.query(packageName);
//        if (volumes != null && volumes.size() != 0) {
//            volume = volumes.get(0);
//        }
//        if (volume == null) {
//            volume = Utils.getSystemVolume(context);
//        }
//        Volume currentVloume = Utils.getCurrentVolume(context, packageName);
//        for (int i = 0; i < currentVloume.getValues().length; i++) {
//            if (currentVloume.getValue(i) != volume.getValue(i)) {
//                Utils.setVolume(context, i, currentVloume.getValue(i));
//            }
//        }
//    }
}
