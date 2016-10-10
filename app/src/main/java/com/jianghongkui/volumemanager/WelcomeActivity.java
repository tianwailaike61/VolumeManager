package com.jianghongkui.volumemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.other.VolumeChangeService;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcom);
        Settings.init(getApplicationContext());
        if (!Utils.isServiceWork(this, VolumeChangeService.class.getSimpleName())) {
            Intent intent = new Intent(this, VolumeChangeService.class);
            startService(intent);
        }
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

}
