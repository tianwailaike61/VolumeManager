package com.jianghongkui.volumemanager.util;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class MLog {
    public static void e(String tag, String msg) {
        android.util.Log.e("jhk", tag + ":" + msg);
    }

    public static void d(String tag, String msg) {
        android.util.Log.d("jhk", tag + ":" + msg);
    }

    public static void v(String tag, String msg) {
        android.util.Log.v("jhk", tag + ":" + msg);
    }
}
