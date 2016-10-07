package com.jianghongkui.volumemanager.model;

import com.jianghongkui.volumemanager.util.MLog;

/**
 * Created by pc on 16-10-4.
 */

public class VolumeObserver {
    private Volume volume;


    public VolumeObserver(Volume volume) {
        this.volume = volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public void update(int type, int value) {
        MLog.d("VolumeObserver","update type:"+type+" value:"+value);
        if (volume != null)
            volume.setValue(type, value);
    }

    public Volume getVolume() {
        return volume;
    }
}
