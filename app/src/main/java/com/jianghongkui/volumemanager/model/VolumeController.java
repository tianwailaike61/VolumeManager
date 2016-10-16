package com.jianghongkui.volumemanager.model;

import android.content.Context;

import com.jianghongkui.volumemanager.other.Application;
import com.jianghongkui.volumemanager.other.PackageVolumeProvider;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class VolumeController implements VolumeContainer.ClickListener {
    private final static String TAG = "VolumeController";
    private VolumeContainer container;
    private VolumeDBManager manager;
    private Context context;
    private String packageName;
    //private Volume volumeInDatebase;
    private Volume systemVolume;

    public VolumeController(Context context, VolumeContainer container) {
        this.container = container;
        this.container.setListener(this);
        this.context = context;
        if (manager == null) {
            manager = VolumeDBManager.newInstace(context);
        }
        if (systemVolume == null) {
            systemVolume = manager.query(Application.SYSTEM).get(0);
        }
    }

    public VolumeController(Context context, VolumeContainer container, String packageName) {
        this(context, container);
        this.packageName = packageName;
        setPackageName(packageName);
    }

    private void setPackageName(String packageName) {
        MLog.d(TAG, "setPackageName-" + packageName);
        this.packageName = packageName;
        ArrayList<Volume> volumes = (ArrayList<Volume>) manager.query(packageName);
        MLog.d(TAG, "setPackageName-database has the volumes:" + volumes);
        if (volumes != null && volumes.size() != 0) {
            Volume volumeInDatebase = volumes.get(0);
            MLog.d(TAG, "setPackageName-database has the data:" + volumeInDatebase);
            container.setVolume(volumeInDatebase);
        }
    }

    private boolean needSave(Volume oldVolume, Volume newVolume) {
        if (newVolume != null) {
            if (oldVolume == null) {
                if (!newVolume.isFollowSystem())
                    return true;
            } else {
                if (oldVolume.isFollowSystem() && newVolume.isFollowSystem())
                    return false;
                else {

//                    if (Arrays.equals(oldVolume.getValues(), newVolume.getValues())) {
//                        return false;
//                    }
                    return true;
                }
            }

        }
        return false;
    }

    public void maybeSaveChanges() {
        Volume newVolume = container.getVolume();
        ArrayList<Volume> volumes = (ArrayList<Volume>) manager.query(packageName);
        Volume volumeInDatebase = null;
        if (volumes != null && volumes.size() != 0) {
            volumeInDatebase = volumes.get(0);
        }
        MLog.d(TAG, "maybeSaveChanges==volumeInDatebase:"
                + volumeInDatebase + "=newVolume:" + newVolume);
        if (needSave(volumeInDatebase, newVolume)) {
            saveChanges(newVolume);
        }
    }

    public void restoreVolume() {
        Volume volume = null;
        ArrayList<Volume> volumes = (ArrayList<Volume>) manager.query(Application.PACKAGENAME);
        if (volumes == null || volumes.size() == 0) {
            volume = manager.query(Application.SYSTEM).get(0);
        } else {
            volume = volumes.get(0);
        }
        int[] currentValues = Utils.getCurrentVolume(context);
        int[] values = volume.getValues();
        MLog.d(TAG, "restoreVolume:" + volume);
        for (int i = 0; i < currentValues.length; i++) {
            if (currentValues[i] != values[i]) {
                Utils.setVolume(context, i, values[i]);
            }
        }

    }

    private void saveChanges(Volume volume) {
        MLog.d(TAG, "saveChanges-" + volume);
        List<Volume> volumes = manager.query(volume.getPackageName());
        if (volumes == null || volumes.size() == 0) {
            manager.insert(volume);
        } else {
            manager.update(volume);
        }

    }

    @Override
    public void onCheckChanged(boolean isChecked) {
        MLog.e(TAG, "onCheckChanged-" + isChecked);
        if (!isChecked && container.getVolume() == null) {
            MLog.e(TAG, "onCheckChanged-container.setVolume:" + systemVolume);
            Volume volume = systemVolume.clone();
            volume.setId(-1);
            volume.setPackageName(packageName);
            volume.setFollowSystem(false);
            container.setVolume(volume);
        }
        container.setEnabled(!isChecked);
    }
}
