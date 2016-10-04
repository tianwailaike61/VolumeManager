package com.jianghongkui.volumemanager.model;

import android.content.Context;

import com.jianghongkui.volumemanager.other.Application;
import com.jianghongkui.volumemanager.util.VolumeDBManager;

import java.util.ArrayList;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class VolumeController implements VolumeContainer.ClickListener {
    private VolumeContainer container;
    private VolumeDBManager manager;
    private Context context;
    private String packageName;
    private Volume oldVolume;
    private Volume systemVolume;

    public VolumeController(Context context, VolumeContainer container) {
        this.container = container;
        this.container.setListener(this);
        this.context = context;
    }

    public VolumeController(Context context, VolumeContainer container, String PackageName) {
        this.container = container;
        this.container.setListener(this);
        this.context = context;
        setPackageName(packageName);
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
        manager = VolumeDBManager.newInstace(context);
        if (systemVolume == null) {
            systemVolume = manager.query(Application.PACKAGENAME).get(0);
        }
        ArrayList<Volume> volumes = (ArrayList<Volume>) manager.query(packageName);
        if (volumes != null && volumes.size() != 0) {
            oldVolume = volumes.get(0);
            container.setVolume(oldVolume);
        }
    }

    public void maybeSaveChanges() {
        Volume newVolume = container.getVolume();
        if (newVolume.equals(oldVolume)) {
            saveChanges(newVolume);
        }
    }

    private void saveChanges(Volume volume) {
        manager.insert(volume);
    }

    @Override
    public void onCheckChanged(boolean isChecked) {
        container.setEnabled(!isChecked);
    }
}
