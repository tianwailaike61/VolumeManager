package com.jianghongkui.volumemanager.model;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jianghongkui.volumemanager.VolumeView;
import com.jianghongkui.volumemanager.util.MLog;

/**
 * Created by jianghongkui on 2016/9/15.
 */
public class VolumeContainer implements CheckBox.OnCheckedChangeListener {
    private final static String TAG = "VolumeContainer";
    private VolumeView[] views;
    private ViewGroup volumeGroup;
    private AppCompatCheckBox followSystem;


    private ClickListener listener;
    //private Volume volume;
    private VolumeObserver volumeObserver;

    public interface ClickListener {
        void onCheckChanged(boolean isChecked);
    }

    public VolumeContainer(ViewGroup volumeGroup, AppCompatCheckBox followSystem) {
        this(volumeGroup, followSystem, null);
    }

    public VolumeContainer(ViewGroup volumeGroup, AppCompatCheckBox followSystem, Volume volume) {
        //this.volume = volume;
        volumeObserver = new VolumeObserver(volume);
        this.volumeGroup = volumeGroup;
        this.followSystem = followSystem;
        initView();
    }

    private void initView() {
        int sum = volumeGroup.getChildCount();
        views = new VolumeView[sum];
        //VolumeObserver observer=new VolumeObserver(volume);
        for (int i = 0; i < sum; i++) {
            views[i] = (VolumeView) volumeGroup.getChildAt(i);
            views[i].setVolumeType(i);
            views[i].setVolumeObserver(volumeObserver);
            if (volumeObserver.getVolume() != null)
                views[i].setVolumeValue(volumeObserver.getVolume().getValue(i));
        }
        followSystem.setOnCheckedChangeListener(this);
        if (volumeObserver.getVolume() != null)
            followSystem.setChecked(volumeObserver.getVolume().isFollowSystem());
        if (followSystem.isChecked()) {
            setEnabled(false);
        }
    }

    public void setVolume(Volume volume) {
        MLog.e(TAG, "setVolume:" + volume);
        //this.volume = volume;
        volumeObserver.setVolume(volume);
        if (volume != null) {
            for (int i = 0; i < views.length; i++) {
                int volumeType = views[i].getVolumeType();
                views[i].setVolumeValue(volume.getValue(volumeType));
            }
            followSystem.setChecked(volume.isFollowSystem());
        }

    }

    public Volume getVolume() {
        return volumeObserver.getVolume();
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
        if (volumeObserver.getVolume() != null)
            volumeObserver.getVolume().setFollowSystem(isChecked);
    }

}
