package com.jianghongkui.volumemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.jianghongkui.volumemanager.model.VolumeObserver;
import com.jianghongkui.volumemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class VolumeView extends RelativeLayout {

    @BindView(R.id.view_volume_value)
    TextView viewVolumeValue;
    @BindView(R.id.view_volume_type)
    TextView viewVolumeType;
    @BindView(R.id.volume_seekbar)
    VerticalSeekBar volumeSeekbar;

    private Context mContext;

    private VolumeObserver observer;

    private int volumeMax;
    private int streamType;

//    private final static int ALARM = 0;
//    private final static int MUSIC = 1;
//    private final static int VOICE_CALL = 2;
//    private final static int RING = 3;


    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_volume, this);
        ButterKnife.bind(view);
    }

    public void addObserver(VolumeObserver observer) {
        this.observer = observer;
    }

    private void notifyObserver(int value) {
        if (observer != null)
            observer.update(streamType, value);
    }


    private void setSeekChangedListener() {
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolumeValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utils.setVolume(mContext, streamType, seekBar.getProgress());
                notifyObserver(seekBar.getProgress());
            }
        });
    }

    public void setVolumeValue(int volumeValue) {
        viewVolumeValue.setText(Utils.formatPercentage(volumeValue, volumeMax));
    }

    public void setCanDragged(final Boolean canDragged) {
        volumeSeekbar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (canDragged)
                    return false;
                else
                    return true;
            }
        });
    }

    public void setVolumeType(int volumeType) {
        String[] strings = mContext.getResources().getStringArray(R.array.volume_type);
        viewVolumeType.setText(strings[volumeType]);
        streamType = volumeType;// getStreamType(volumeType);
        volumeMax = Utils.getMaxVolume(mContext, streamType);
        int volumevalue = Utils.getVolume(mContext, streamType);
        setVolumeValue(volumevalue);
        setSeekChangedListener();
        volumeSeekbar.setMax(volumeMax);
        volumeSeekbar.setProgress(volumevalue);
    }

    public int getVolumeType() {
        return streamType;
    }

//    private int getStreamType(int volumeType) {
//        int streamtype = AudioManager.STREAM_MUSIC;
//        switch (volumeType) {
//            case ALARM:
//                streamtype = AudioManager.STREAM_ALARM;
//                break;
//            case MUSIC:
//                streamtype = AudioManager.STREAM_MUSIC;
//                break;
//            case VOICE_CALL:
//                streamtype = AudioManager.STREAM_VOICE_CALL;
//                break;
//            case RING:
//                streamtype = AudioManager.STREAM_RING;
//                break;
//        }
//        MLog.e("" + streamtype);
//        return streamtype;
//    }
}
