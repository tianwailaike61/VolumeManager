package com.jianghongkui.volumemanager;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

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

    private AudioManager am;

    private int volumeMax;
    private int streamType;

    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        View view = LayoutInflater.from(context).inflate(R.layout.view_volume, this);
        ButterKnife.bind(view);
    }


    private void setSeekChangedListener() {
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Utils.setVolume(mContext,streamType,progress);
                setVolumeValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setVolumeValue(int volumeValue) {
        viewVolumeValue.setText(Utils.formatPercentage(volumeValue, volumeMax));
    }

    public void setVolumeType(int volumeType) {
        String[] strings = mContext.getResources().getStringArray(R.array.volume_type);
        viewVolumeType.setText(strings[volumeType]);
        streamType = getStreamType(volumeType);
        volumeMax = am.getStreamMaxVolume(streamType);
        int volumevalue=am.getStreamVolume(streamType);
        setVolumeValue(volumevalue);
        setSeekChangedListener();
        volumeSeekbar.setMax(volumeMax);
        volumeSeekbar.setProgress(volumevalue);
    }

    private int getStreamType(int volumeType) {
        int streamtype = AudioManager.STREAM_MUSIC;
        switch (volumeType) {
            case 0:
                streamtype = AudioManager.STREAM_ALARM;
                break;
            case 1:
                streamtype = AudioManager.STREAM_MUSIC;
                break;
            case 2:
                streamtype = AudioManager.STREAM_VOICE_CALL;
                break;
            case 3:
                streamtype = AudioManager.STREAM_RING;
                break;
        }
        MLog.e("--" + streamtype);
        return streamtype;
    }
}
