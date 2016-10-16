package com.jianghongkui.volumemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.other.VolumeChangeService;
import com.jianghongkui.volumemanager.util.Utils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcom);
        if (!Utils.isServiceWork(this, VolumeChangeService.class.getSimpleName())) {
            Intent intent = new Intent(this, VolumeChangeService.class);
            startService(intent);
        }
        //setActivityController();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(WelcomeActivity.this, VolumeActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private void setActivityController() {
//        try {
//            MLog.e("setActivityController", "1");
//            Class<?> cActivityManagerNative = Class
//                    .forName("android.app.ActivityManagerNative");
//            MLog.e("setActivityController", "2");
//            Method mGetDefault = cActivityManagerNative.getMethod("getDefault",
//                    null);
//            MLog.e("setActivityController", "3");
//            Object oActivityManagerNative = mGetDefault.invoke(null, null);
//            Class<?> i = Class.forName("android.app.IActivityController$Stub");
//            MLog.e("setActivityController", "4");
//            Method mSetActivityController = cActivityManagerNative.getMethod(
//                    "setActivityController",
//                    //      i);
//                    Class.forName("android.app.IActivityController"));
//            MLog.e("setActivityController", "5");
//            Class<?> class1 = Class.forName("android.app.IActivityController");
//            MLog.e("setActivityController", "5-0");
//            Method asInterface = class1.
//                    getMethod("asInterface", Class.forName("android.os.IBinder"));
//            MLog.e("setActivityController", "5-1");
//            Object object = asInterface.invoke(null, new ActivityController());
//            MLog.e("setActivityController", "5-2");
//            mSetActivityController.invoke(oActivityManagerNative, object);
//            IActivityController.Stub.asInterface(new ActivityController());
//            MLog.e("setActivityController", "6");
//        } catch (ClassNotFoundException e) {
//            MLog.e("setActivityController", "7");
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            MLog.e("setActivityController", "8");
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            MLog.e("setActivityController", "9");
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            MLog.e("setActivityController", "10");
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            MLog.e("setActivityController", "11");
//            e.printStackTrace();
//        }
//    }
//

}
