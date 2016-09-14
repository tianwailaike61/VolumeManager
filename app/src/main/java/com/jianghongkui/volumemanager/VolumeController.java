package com.jianghongkui.volumemanager;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class VolumeController {
    VolumeView[] views;
    ViewGroup parent;

    public VolumeController(Context context, ViewGroup parent) {
        this.parent = parent;
        int sum = parent.getChildCount();
        views = new VolumeView[sum];
        for (int i = 0; i < sum; i++) {
            views[i] = (VolumeView) parent.getChildAt(i);
            views[i].setVolumeType(i);
        }
    }
}
