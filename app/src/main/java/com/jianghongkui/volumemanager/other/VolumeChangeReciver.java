package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jianghongkui.volumemanager.util.MLog;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        MLog.d(TAG, "onReceive:" + intent);
        if (APP_CHANGE_VOLUME.equals(action)) {
            flag = false;
            String appName = intent.getStringExtra("Name");
            String volumeType = intent.getStringExtra("Type");
            Toast.makeText(context, appName + " is open,app help to change the " + volumeType + " volume!", Toast.LENGTH_SHORT).show();
        }
        if (USER_CHANGE_VOLUME.equals(action)) {
            if (flag) {
                MLog.d(TAG, "user change the volume");
                if (Application.saveUserChanges) {
                    Intent intent1 = new Intent();
                    intent1.setAction(ACTION_USER_ADJUST_VOLUME);
                    context.sendBroadcast(intent1);
                }
            } else {
                MLog.d(TAG, "this app change the volume");
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
}
