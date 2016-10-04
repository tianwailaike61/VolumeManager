package com.jianghongkui.volumemanager.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jianghongkui.volumemanager.model.Column;

/**
 * Created by jianghongkui on 2016/9/18.
 */
public class VolumeDBHelper extends SQLiteOpenHelper {

    private final static String NAME = "Volumes.db";
    private final static int VERSION = 1;

    public final static String TABLE = "Volume";

    public VolumeDBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE + "(" +
                Column._id + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Column._PackageName + " Text NOT NULL," +
                Column._Voice_Call + " INTEGER ," +
                Column._System + " INTEGER ," +
                Column._Ring + " INTEGER ," +
                Column._Music + " INTEGER ," +
                Column._Alarm + " INTEGER ," +
                Column._Notification + " INTEGER ," +
//                Column._Bluetooth_Call + " INTEGER ," +
//                Column._System_Enforced + " INTEGER ," +
//                Column._DTMF + " INTEGER ," +
//                Column._TTS + " INTEGER ," +
                Column._Follow_System + " INTEGER" +
                ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
