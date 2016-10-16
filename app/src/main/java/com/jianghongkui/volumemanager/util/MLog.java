package com.jianghongkui.volumemanager.util;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class MLog {
    private static boolean debug = false;

    public static void e(String tag, String msg) {
        if (debug)
            android.util.Log.e("jhk", tag + ":" + msg);
    }

    public static void d(String tag, String msg) {
        if (debug)
            android.util.Log.d("jhk", tag + ":" + msg);
    }

    public static void v(String tag, String msg) {
        if (debug)
            android.util.Log.v("jhk", tag + ":" + msg);
    }
}
