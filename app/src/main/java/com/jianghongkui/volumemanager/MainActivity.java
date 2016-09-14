package com.jianghongkui.volumemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.volumegroup)
    LinearLayout volumegroup;
    private MReceiver mReceiver;

    private final static String INTENT_ACTION = "com.example.test";

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        test();
        init();

    }

    private void test() {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_ALARM, 3, AudioManager.FLAG_PLAY_SOUND);
        MLog.e("alarm--" + am.getStreamVolume(AudioManager.STREAM_ALARM));
    }


    public void init() {
        VolumeController controller = new VolumeController(this, volumegroup);

    }


//    ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //返回一个MsgService对象
//            packageService = ((packageService.MBinder) service).getService();
//        }
//    };

    public class MReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.clear();
            ArrayList<String> datas = new ArrayList<>();
            if (INTENT_ACTION.equals(intent.getAction())) {

            }
            adapter.addAll(datas);
            adapter.notifyDataSetChanged();
        }
    }
}
