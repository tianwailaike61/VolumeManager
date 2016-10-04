package com.jianghongkui.volumemanager.model;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jianghongkui.volumemanager.VolumeView;

/**
 * Created by jianghongkui on 2016/9/15.
 */
public class VolumeContainer implements CheckBox.OnCheckedChangeListener {
    private VolumeView[] views;
    private ViewGroup volumeGroup;
    private AppCompatCheckBox followSystem;

    private VolumeObserver observer;

    private ClickListener listener;


    public interface ClickListener {
        void onCheckChanged(boolean isChecked);
    }

    public VolumeContainer(ViewGroup volumeGroup, AppCompatCheckBox followSystem) {
        this.volumeGroup = volumeGroup;
        this.followSystem = followSystem;
        initView();
    }

    private void initView() {
        int sum = volumeGroup.getChildCount();
        views = new VolumeView[sum];
        for (int i = 0; i < sum; i++) {
            views[i] = (VolumeView) volumeGroup.getChildAt(i);
            views[i].setVolumeType(i);
            if (observer != null)
                views[i].addObserver(observer);
        }
        followSystem.setOnCheckedChangeListener(this);
        if (followSystem.isChecked()) {
            setEnabled(false);
        }
    }

    public void setVolume(Volume volume) {
        observer = new VolumeObserver(volume);
        if (volume != null) {
            for (int i = 0; i < views.length; i++) {
                int volumeType = views[i].getVolumeType();
                views[i].setVolumeValue(volume.getValue(volumeType));
                if (observer != null)
                    views[i].addObserver(observer);
            }
        }
        followSystem.setChecked(volume.isFollowSystem());
    }

    public Volume getVolume() {
        Volume volume = new Volume();
        volume.setFollowSystem(followSystem.isChecked());
        int[] values = new int[Column.count];
        //TODO....
        volume.setValues(values);
        return volume;
    }

    public void setEnabled(boolean enabled) {
        volumeGroup.setEnabled(enabled);
        int sum = volumeGroup.getChildCount();
        for (int i = 0; i < sum; i++) {
            views[i].setCanDragged(enabled);
        }
    }


    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        listener.onCheckChanged(isChecked);
    }

    private void changeVolumeView(int order, int type) {
        views[order].setVolumeType(type);
    }
}
