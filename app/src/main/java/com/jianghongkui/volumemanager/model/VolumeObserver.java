package com.jianghongkui.volumemanager.model;


import com.jianghongkui.volumemanager.util.MLog;

public class VolumeObserver {
    private final static String TAG = "VolumeObserver";
    private Volume volume;

    public VolumeObserver(Volume volume) {
        this.volume = volume;
    }

    public void update(int type, int value) {
        MLog.d(TAG, "update-type:" + type + "-value:" + value);
        volume.setValue(type, value);
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }
}
