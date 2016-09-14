package com.jianghongkui.volumemanager;

import android.content.Context;
import android.media.AudioManager;

import java.text.NumberFormat;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class Utils {

    public static int getVolume(Context context, int streamType) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(streamType);
    }

    public static void setVolume(Context context, int streamType, int index) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(streamType, index, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    public static void adjustVolume(Context context, int streamType, int direction) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(streamType, direction, AudioManager.FLAG_PLAY_SOUND);
    }

    public static String formatPercentage(int child, int parent) {
        if (child >= 0 && parent >= 0) {
            double percent = (1.0 * child) / parent;
            NumberFormat nt = NumberFormat.getPercentInstance();
            nt.setMinimumFractionDigits(0);
            return "" + nt.format(percent);
        }
        return null;
    }
}
