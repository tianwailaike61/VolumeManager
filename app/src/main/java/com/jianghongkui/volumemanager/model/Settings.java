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

    public static boolean saveUserChanges = false;
    public static boolean isSaveIntoSystem = true;
    public static boolean showNotification = true;
    public static boolean allowSelfStart = true;

    public static List<String> list = new ArrayList<>();

    static {
        list.add("com.android.systemui");
    }

    public static void init(Context context) {
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
                R.string.preference_notification), false);
    }
}
