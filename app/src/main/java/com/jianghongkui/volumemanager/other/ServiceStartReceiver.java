package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.util.Utils;

/**
 * Created by jianghongkui on 2016/10/9.
 */

public class ServiceStartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Settings.allowSelfStart && !Utils.isServiceWork(context, VolumeChangeReciver.class.getSimpleName())) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_TIME_TICK)) {
                Intent intent1 = new Intent(context, VolumeChangeService.class);
                context.startService(intent1);
            }
        }
    }


}
