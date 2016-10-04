package com.jianghongkui.volumemanager.other;

import android.content.SharedPreferences;

import com.jianghongkui.volumemanager.model.Column;
import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

/**
 * Created by jianghongkui on 2016/9/27.
 */

public class Application extends android.app.Application {

    public final static String PACKAGENAME = "com.jianghongkui.volumemanager";
    public static boolean firstBoot = true;

    public static boolean saveUserChanges = false;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = getSharedPreferences("FLAG", MODE_PRIVATE);
        if (sp.getBoolean("FirstBoot", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("FirstBoot", false);
            editor.commit();
            initSystemVolume();
        } else {
            firstBoot = false;
        }
    }

    private void initSystemVolume() {
        Volume system = new Volume();
        system.setFollowSystem(true);
        system.setPackageName(PACKAGENAME);
        int[] values = new int[Column.count];
        for (int i = 0; i < Column.count; i++) {
            values[i] = Utils.getVolume(this, i);
        }
        system.setValues(values);
        VolumeDBManager.newInstace(this).insert(system);
    }
}
