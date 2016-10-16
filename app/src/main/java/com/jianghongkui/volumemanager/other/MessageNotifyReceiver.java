package com.jianghongkui.volumemanager.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.INotificationSideChannel;
import android.widget.Toast;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.model.Notice;
import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.util.MLog;

import java.util.ArrayList;

/**
 * Created by pc on 16-10-16.
 */

public class MessageNotifyReceiver extends BroadcastReceiver {
    private final static String TAG = "MessageNotifyReceiver";

    public final static String ACTION_MESSAGE_NOTIFY = "com.action.message_notify";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.d(TAG, "MessageNotifyReceiver onReceive:" + intent);
        this.context = context;
        if (ACTION_MESSAGE_NOTIFY.equals(intent.getAction())) {
            if (Settings.showNotification) {
                Intent newIntent = new Intent();
                newIntent.setAction(VolumeChangeService.ACTION_NOTIFICATION_MASSAFE_CHANGED);
                newIntent.putExtra("Message", getMessage(intent));
                context.sendBroadcast(newIntent);
            } else {
                Toast.makeText(context, getMessage(intent), Toast.LENGTH_LONG).show();
            }
        }
    }

    private String getMessage(Intent intent) {
        Notice notice = intent.getParcelableExtra(Notice.KEY);
        switch (notice.getType()) {
            case Notice.CHANGE_VOLUME:
                return getMessageForChangeVolume(notice);
            case Notice.SAVE_VOLUME:
                return getMessageForSaveVolume(notice);
            default:
                return "";
        }
    }

    private String getMessageForChangeVolume(Notice notice) {
        StringBuffer stringBuffer = new StringBuffer();
        ArrayList<Integer> integers = notice.getIntegers();
        stringBuffer.append(String.format(context.getString(R.string.notification_msg_change_volume), notice.getName()));
        if (integers != null && integers.size() != 0) {
            String[] strings = context.getResources().getStringArray(R.array.volume_type);
            StringBuffer buffer = new StringBuffer();
            MLog.d(TAG, "integers:" + integers + " " + integers.size());
            for (Integer integer : integers) {
                if (integer.equals(new Integer("0"))) {
                    buffer.append(strings[0] + ",");
                } else if (integer.equals(new Integer("3"))) {
                    buffer.append(strings[3] + ",");
                }
            }
            buffer.deleteCharAt(buffer.length() - 1);
            stringBuffer.append(String.format(context.getString(R.string.notification_msg_change_volume_extra), buffer.toString()));
        }
        stringBuffer.append("!");
        return stringBuffer.toString();
    }

    private String getMessageForSaveVolume(Notice notice) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.format(context.getString(R.string.notification_msg_save_volume), notice.getName()));
        return stringBuffer.toString();
    }
}
