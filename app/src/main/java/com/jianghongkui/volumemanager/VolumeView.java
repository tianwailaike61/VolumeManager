package com.jianghongkui.volumemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.jianghongkui.volumemanager.model.VolumeObserver;
import com.jianghongkui.volumemanager.other.Application;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class VolumeView extends RelativeLayout {
    private final static String TAG = "VolumeView";
    @BindView(R.id.view_volume_value)
    TextView viewVolumeValue;
    @BindView(R.id.view_volume_type)
    TextView viewVolumeType;
    @BindView(R.id.volume_seekbar)
    VerticalSeekBar volumeSeekbar;

    private Context mContext;


    private int volumeMax;
    private int streamType;
    private int streamValue;

    private VolumeObserver volumeObserver;

//    private final static int ALARM = 0;
//    private final static int MUSIC = 1;
//    private final static int VOICE_CALL = 2;
//    private final static int RING = 3;

    private final static String action = Application.PACKAGENAME + ".VolumeView_Change";

    private class VolumeViewChangeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "onReceiver:" + intent + "-type:" + intent.getIntExtra("streamType", -1) + "-" + streamType);
            if (action.equals(intent.getAction())) {
                int type = intent.getIntExtra("streamType", -1);
                if (type != -1) {
                    if (type != streamType) {
                        int value = Utils.getVolume(context, streamType);
                        MLog.d(TAG, "onReceiver:value:" + value);
                        if (value != volumeSeekbar.getProgress()) {
                            MLog.d(TAG, "onReceiver:setProgresss");
                            volumeSeekbar.setProgress(value);
                            if (volumeObserver != null) {
                                volumeObserver.update(streamType, value);
                            }
                        }
                    } else {
                        //int value = intent.getIntExtra("streamValue", 0);
                        //Utils.setVolume(context, streamType, streamValue);
                    }
                }
            }
        }
    }


    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_volume, this);
        ButterKnife.bind(view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        context.registerReceiver(new VolumeViewChangeReciver(), filter);
    }

    public void setVolumeObserver(VolumeObserver volumeObserver) {
        this.volumeObserver = volumeObserver;
    }

    private void setSeekChangedListener() {
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean isTouched = false;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setVolumeValue(progress);
                if (isTouched) {
                    Utils.setVolume(mContext, streamType, seekBar.getProgress(), null);
                    Intent intent = new Intent();
                    intent.setAction(action);
                    intent.putExtra("streamType", streamType);
                    mContext.sendBroadcast(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouched = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouched = false;
                if (volumeObserver != null) {
                    volumeObserver.update(streamType, seekBar.getProgress());
                }
            }
        });
    }

    private void setVolumeValue(int volumeValue) {
        viewVolumeValue.setText(Utils.formatPercentage(volumeValue, volumeMax));
    }

    public void setVolumeValue(long volumeValue) {
        volumeSeekbar.setProgress((int) volumeValue);
    }

    public int getVolumeValue() {
        return volumeSeekbar.getProgress();
    }

    public void setCanDragged(final Boolean canDragged) {
        volumeSeekbar.setEnabled(canDragged);
        //setGradients(canDragged ? Color.WHITE : Color.GRAY);
        //volumeSeekbar.setBackgroundColor(canDragged ? Color.WHITE : Color.GRAY);
//        volumeSeekbar.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (canDragged) {
//                    volumeSeekbar.setEnabled(true);
//                    return false;
//                } else {
//                    volumeSeekbar.setEnabled(false);
//                    return true;
//                }
//            }
//        });
    }

    public void setVolumeType(int volumeType) {
        String[] strings = mContext.getResources().getStringArray(R.array.volume_type);
        viewVolumeType.setText(strings[volumeType]);
        streamType = volumeType;// getStreamType(volumeType);
        volumeMax = Utils.getMaxVolume(mContext, streamType);
        //streamValue = Utils.getVolume(mContext, streamType);
        //setVolumeValue(streamValue);
        setSeekChangedListener();
        volumeSeekbar.setMax(volumeMax);
        //volumeSeekbar.setProgress(streamValue);
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
