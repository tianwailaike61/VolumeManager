package com.jianghongkui.volumemanager;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.other.NotificationService;
import com.jianghongkui.volumemanager.util.MLog;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private final static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);

        findPreference(getString(R.string.preference_notification))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_save_users_change))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_question))
                .setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_other_function))
                .setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.preference_other_function))) {
            Toast.makeText(this, R.string.wait, Toast.LENGTH_LONG).show();
        }
        if (preference.getKey().equals(getString(R.string.preference_question))) {
            new AlertDialog.Builder(this).setTitle(R.string.preference_question_description)
                    .setMessage(R.string.preference_question_content).show();
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.preference_notification))) {
            boolean flag = (boolean) newValue;
            MLog.e(TAG, "onPreferenceChange");
            if (flag) {
                startNotification();
            } else {
                stopNotification();
            }
        }
        if (preference.getKey().equals(getString(R.string.preference_save_users_change))) {
            Settings.saveUserChanges = (boolean) newValue;
        }
        return true;
    }

    private void startNotification() {
//        Intent intent = new Intent();
//        intent.setAction(VolumeChangeService.ACTION_NOTIFICATION_CHANGED);
//        intent.putExtra("state","on");
//        sendBroadcast(intent);
        Intent intent = new Intent(SettingsActivity.this, NotificationService.class);
        startService(intent);
    }

    private void stopNotification() {
//        Intent intent = new Intent();
//        intent.setAction(VolumeChangeService.ACTION_NOTIFICATION_CHANGED);
//        intent.putExtra("state","off");
//        sendBroadcast(intent);
        Intent intent = new Intent(this, NotificationService.class);
        stopService(intent);
    }
}
