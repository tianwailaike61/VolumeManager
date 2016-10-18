package com.jianghongkui.volumemanager.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.other.Application;
import com.jianghongkui.volumemanager.util.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 16-10-7.
 */

public class Settings {

    private final static String NAME = "com.jianghongkui.volumemanager_preferences";//Application.PACKAGENAME + "__preferences";

    private static boolean isInited = false;
    public static boolean saveUserChanges = false;
    public static boolean isSaveIntoSystem = true;
    public static boolean showNotification = true;
    public static boolean allowSelfStart = true;

    public static boolean isMutedModel = false;

    public static boolean forceChangeMusic = false;
    public static boolean forceChangeVioceCall = false;

    public static List<String> list = new ArrayList<>();

    static {
        list.add("com.android.systemui");
        list.add("com.android.launcher3");
        list.add("com.android.settings");
        list.add("com.jianghongkui.volumemanager");
    }

    public static void init(Context context) {
        if (!isInited) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
            saveUserChanges = sharedPreferences.getBoolean(context.getString(
                    R.string.preference_save_users_change), false);
            String flag = sharedPreferences.getString(context.getString(
                    R.string.preference_save_type), "");
            if (TextUtils.isEmpty(flag) || flag.equals("1")) {
                isSaveIntoSystem = true;
            } else {
                isSaveIntoSystem = false;
            }
            showNotification = sharedPreferences.getBoolean(context.getString(
                    R.string.preference_notification), true);
            forceChangeVioceCall = sharedPreferences.getBoolean(context.getString(
                    R.string.preference_force_change_voice_call), false);
            forceChangeMusic = sharedPreferences.getBoolean(context.getString(
                    R.string.preference_force_change_music), false);
        }
    }
}
