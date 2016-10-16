package com.jianghongkui.volumemanager.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jianghongkui.volumemanager.model.Column;
import com.jianghongkui.volumemanager.model.Volume;

import java.text.NumberFormat;
import java.util.List;

import static com.jianghongkui.volumemanager.other.Application.PACKAGENAME;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class Utils {

    /**
     * @return RINGER_MODE_NORMAL,
     * RINGER_MODE_SILENT,
     * RINGER_MODE_VIBRATE
     */
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
        setVolume(context, streamType, index, null);
    }

    public static int[] getCurrentVolume(Context context) {
        int[] values = new int[Column.count];
        for (int i = 0; i < Column.count; i++) {
            values[i] = getVolume(context, i);
        }
        return values;
    }

    public static void setVolume(Context context, int streamType, int index, @Nullable Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction("com.action.app_set_volume");
        intent.putExtra("VolumeType", streamType);
        intent.putExtra("VolumeValue", index);
        if (bundle != null) {
            intent.putExtras(bundle);
//            intent.putExtra("Name", bundle.getString("Name"));
//            intent.putExtra("NotChangeVolumeTypes", bundle.getIntegerArrayList("NotChangeVolumeTypes"));
        }
        context.sendBroadcast(intent);
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(streamType, index, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    public static String getAppName(Context context, String packageName) {
        PackageManager packageManager = null;
        PackageInfo info = null;
        try {
            packageManager = context.getPackageManager();
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            packageManager = null;
            info = null;
        }
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

    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
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
