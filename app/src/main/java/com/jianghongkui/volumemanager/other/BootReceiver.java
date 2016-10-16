package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;

/**
 * Created by jianghongkui on 2016/10/9.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Settings.allowSelfStart && !Utils.isServiceWork(context, VolumeChangeReciver.class.getSimpleName())) {
            Intent intent1 = new Intent(context, VolumeChangeService.class);
            context.startService(intent1);
        }

    }


}
