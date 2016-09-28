package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jianghongkui on 2016/9/14.
 */
public class PacakgeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //接收安装广播
        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            System.out.println("安装了:" + packageName + "包名的程序");
        }
        //接收卸载广播
        if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            System.out.println("卸载了:" + packageName + "包名的程序");

        }
    }
}