package com.jianghongkui.volumemanager.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.text.NumberFormat;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class Utils {

    public static int getRingerMode(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode();
    }

    public static int getVolume(Context context, int streamType) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(streamType);
    }

    public static int getMaxVolume(Context context, int streamType) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamMaxVolume(streamType);
    }

    public static void setVolume(Context context, int streamType, int index) {
        Intent intent = new Intent();
        intent.setAction("com.action.app_set_volume");
        intent.putExtra("VolumeType", streamType);
        intent.putExtra("VolumeValue", index);
        context.sendBroadcast(intent);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(streamType, index, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    public static void adjustVolume(Context context, int streamType, int direction) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(streamType, direction, AudioManager.FLAG_PLAY_SOUND);
    }

    @Nullable
    public static String formatPercentage(int child, int parent) {
        if (child >= 0 && parent >= 0) {
            double percent = (1.0 * child) / parent;
            NumberFormat nt = NumberFormat.getPercentInstance();
            nt.setMinimumFractionDigits(0);
            return "" + nt.format(percent);
        }
        return null;
    }

    public static Bitmap drawableToBitamp(Drawable drawable, int w, int h) {
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean isAccessibilitySettingsOn(Context mContext, String service) {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {

        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return accessibilityFound;
    }
}
