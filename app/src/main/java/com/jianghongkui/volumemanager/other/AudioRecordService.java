package com.jianghongkui.volumemanager.other;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jianghongkui.volumemanager.util.MLog;

public class AudioRecordService extends Service {
    private static final String TAG = "AudioRecordService";
    private RecordTask recordTask;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recordTask = new RecordTask();
        recordTask.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (recordTask != null)
            recordTask.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class RecordTask extends AsyncTask {
        private final static int SAMPLE_RATE_IN_HZ = 8000;
        private final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT);
        private AudioRecord audioRecord;
        private boolean isGetVoiceRunning = false;

        @Override
        protected void onPreExecute() {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT,
                    BUFFER_SIZE);
            isGetVoiceRunning = true;
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if (audioRecord != null) {
                audioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (isGetVoiceRunning) {
                    //r是实际读取的数据长度，一般而言r会小于buffer size
                    int r = audioRecord.read(buffer, 0, BUFFER_SIZE);
                    double volume = getNoiselevel(buffer, r);
                    MLog.e(TAG, "volume-" + volume);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            isGetVoiceRunning = false;
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        private void stop() {
            isGetVoiceRunning = false;
            this.cancel(true);
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
    }
}
