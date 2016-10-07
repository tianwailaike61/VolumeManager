package com.jianghongkui.volumemanager.other;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;

import com.jianghongkui.volumemanager.util.MLog;

public class AudioRecordService extends Service {
    private static final String TAG = "AudioRecordService";
    private final static int SAMPLE_RATE_IN_HZ = 8000;
    private final static int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord audioRecord;
    private boolean isGetVoiceRunning;

    public AudioRecordService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getNoiselevel();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startRecord() {

    }

    private void pauseRecord() {

    }

    private void stopRecord() {

    }

    public double getNoiselevel(short[] buffer, int r) {
        long v = 0;
        //将buffer内容取出，进行平方和运算
        for (int i = 0; i < buffer.length; i++) {
            v += buffer[i] * buffer[i];
        }
        //平方和除以数据中长度，得到音量大小
        double mean = v / (double) r;
        double volume = 10 * Math.log10(mean);
        return volume;
    }

    public void getNoiselevel() {
        if (isGetVoiceRunning) {
            MLog.e(TAG, "VoiceRunning");
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT,
                BUFFER_SIZE);
        if (audioRecord == null) {
            MLog.e(TAG, "init fail");
        }
        isGetVoiceRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (isGetVoiceRunning) {
                    //r是实际读取的数据长度，一般而言r会小于buffer size
                    int r = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    double volume = getNoiselevel(buffer, r);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }).start();
    }
}
