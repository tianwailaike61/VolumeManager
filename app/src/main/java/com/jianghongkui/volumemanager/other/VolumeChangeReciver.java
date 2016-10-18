package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jianghongkui.volumemanager.model.Notice;
import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.util.MLog;

/**
 * Created by jianghongkui on 2016/9/21.
 */

public class VolumeChangeReciver extends BroadcastReceiver {
    private final String TAG = "VolumeChangeReciver";
    public static final String ACTION_APP_CHANGE_VOLUME = "com.action.app_set_volume";
    public static final String ACTION_USER_CHANGE_VOLUME = "android.media.VOLUME_CHANGED_ACTION";


    private Context context;

    private boolean userschange = false;

    private String name;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        MLog.d(TAG, "onReceive:" + intent);
        if (ACTION_APP_CHANGE_VOLUME.equals(action)) {
            userschange = false;
            Notice notice = intent.getParcelableExtra(Notice.KEY);
            MLog.d(TAG, "notice:" + notice);
            if (notice != null && !notice.getName().equals(name)) {

                Intent newIntent = new Intent(MessageNotifyReceiver.ACTION_MESSAGE_NOTIFY);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(Notice.KEY, notice);
                newIntent.putExtras(mBundle);
                context.sendBroadcast(newIntent);
                name = notice.getName();
            }
        } else if (ACTION_USER_CHANGE_VOLUME.equals(action)) {
            if (userschange) {
                // MLog.d(TAG, "user change the volume");
                if (Settings.showNotification) {
                    Intent newIntent = new Intent(VolumeChangeService.ACTION_NOTIFICATION_NO_VOLUME);
                    newIntent.putExtra("userchange", "true");
                    newIntent.putExtra("isNoVolume", false);
                    context.sendBroadcast(newIntent);
                }
                if (Settings.saveUserChanges) {
                    Intent intent1 = new Intent();
                    intent1.setAction(WindowChangeDetectingService.UserAdjustVolumeReceiver.
                            ACTION_USER_ADJUST_VOLUME);
                    context.sendBroadcast(intent1);
                }
            } else {
                //MLog.d(TAG, "this app change the volume");
                userschange = true;
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
