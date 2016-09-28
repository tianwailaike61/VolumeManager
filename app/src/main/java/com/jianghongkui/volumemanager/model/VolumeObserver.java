package com.jianghongkui.volumemanager.model;


public class VolumeObserver {
    private Volume volume;

    public VolumeObserver(Volume volume) {
        this.volume = volume;
    }

    public void update(int type, int value) {
        volume.setValue(type, value);
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }
}
