package com.jianghongkui.volumemanager;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.jianghongkui.volumemanager.model.Settings;
import com.jianghongkui.volumemanager.other.VolumeChangeService;
import com.jianghongkui.volumemanager.util.MLog;


public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private final static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        addPreferencesFromResource(R.xml.preference_settings);
        setOnPreferenceChangeListener(R.string.preference_notification);
        setOnPreferenceChangeListener(R.string.preference_save_users_change);
        setOnPreferenceChangeListener(R.string.preference_force_change_voice_call);
        setOnPreferenceChangeListener(R.string.preference_force_change_music);

        setOnPreferenceClickListener(R.string.preference_question);
        setOnPreferenceClickListener(R.string.preference_other_function);

        ListPreference preference = (ListPreference) findPreference(getString(R.string.preference_save_type));
        preference.setOnPreferenceChangeListener(this);
        this.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private void setOnPreferenceChangeListener(int strId) {
        findPreference(getString(strId)).setOnPreferenceChangeListener(this);
    }

    private void setOnPreferenceClickListener(int strId) {
        findPreference(getString(strId)).setOnPreferenceClickListener(this);
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
        String key = preference.getKey();
        if (key.equals(getString(R.string.preference_notification))) {
            boolean flag = (boolean) newValue;
            Settings.showNotification = flag;
            MLog.e(TAG, "onPreferenceChange");
            if (flag) {
                startNotification();
            } else {
                stopNotification();
            }
        } else if (key.equals(getString(R.string.preference_save_users_change))) {
            Settings.saveUserChanges = (boolean) newValue;
//            Preference savetype = findPreference(getString(R.string.preference_save_type));
//            if (Settings.isSaveIntoSystem)
//                savetype.setSummary(R.string.preference_save_system);
//            else
//                savetype.setSummary(R.string.preference_save_program);
        } else if (key.equals(getString(R.string.preference_force_change_voice_call))) {
            Settings.forceChangeVioceCall = (boolean) newValue;
        } else if (key.equals(getString(R.string.preference_force_change_music))) {
            Settings.forceChangeMusic = (boolean) newValue;
        } else if (key.equals(getString(R.string.preference_save_type))) {
            String stringValue = newValue.toString();
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            MLog.d(TAG, "index--" + index);
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);
            Settings.isSaveIntoSystem = stringValue.equals("1") ? true : false;
        }
        return true;
    }

    private void startNotification() {
        Intent intent = new Intent();
        intent.setAction(VolumeChangeService.ACTION_NOTIFICATION_STATE_CHANGED);
        intent.putExtra("state", true);
        sendBroadcast(intent);
//        Intent intent = new Intent(SettingsActivity.this, VolumeChangeService.class);
//        startService(intent);
    }

    private void stopNotification() {
        Intent intent = new Intent();
        intent.setAction(VolumeChangeService.ACTION_NOTIFICATION_STATE_CHANGED);
        intent.putExtra("state", false);
        sendBroadcast(intent);
//        Intent intent = new Intent(this, VolumeChangeService.class);
//        stopService(intent);
    }
}
