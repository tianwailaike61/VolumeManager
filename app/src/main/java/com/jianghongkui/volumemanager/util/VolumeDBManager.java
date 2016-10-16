package com.jianghongkui.volumemanager.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.jianghongkui.volumemanager.model.Column;
import com.jianghongkui.volumemanager.other.PackageVolumeProvider;
import com.jianghongkui.volumemanager.model.Volume;

import java.util.ArrayList;
import java.util.List;

public class VolumeDBManager {

    private final static String TAG = "VolumeDBManager";

    private ContentResolver resolver;

    private static VolumeDBManager manager;

    private VolumeDBManager(Context context) {
        resolver = context.getContentResolver();
    }

    public synchronized static VolumeDBManager newInstace(Context context) {
        if (manager == null)
            manager = new VolumeDBManager(context);
        return manager;
    }

    public List<Volume> query(String packageName) {
        List<Volume> volumes = null;
        Uri rowAddress = ContentUris.withAppendedId(PackageVolumeProvider.CONTENT_URI, 1);
        String[] result_columns = null;
        String where;
        if (packageName == null) {
            where = null;
        } else {
            where = Column._PackageName + "='" + packageName + "'";
        }
        String[] whereArgs = null;
        String order = null;
        Cursor cursor = resolver.query(rowAddress, result_columns, where, whereArgs, order);
        if (cursor != null) {
            volumes = new ArrayList<>();
            while (cursor.moveToNext()) {
                volumes.add(getVolumeByCursor(cursor));
            }
        }
        return volumes;
    }

    public boolean insert(Volume volume) {
        ContentValues values = volume.getContentValues();
        Uri uri = null;
        List<Volume> volumes = query(volume.getPackageName());
        MLog.d(TAG, "query when insert :volumes=" + volumes + (volumes != null ? ("size:" + volumes.size()) : ("")));
        if (volumes == null || volumes.size() == 0) {
            uri = resolver.insert(PackageVolumeProvider.CONTENT_URI, values);
        }
        if (uri == null)
            return false;
        return true;
    }

    public boolean update(Volume newVolume) {
        ContentValues values = newVolume.getContentValues();
        String where = Column._PackageName + "='" + newVolume.getPackageName() + "'";
        String[] whereArgs = null;// new String[]{newVolume.getPackageName()};
        int flag = resolver.update(PackageVolumeProvider.CONTENT_URI, values, where, whereArgs);
        if (flag == -1)
            return false;
        return true;
    }

    public boolean delete(String packageName) {
        String where = Column._PackageName + "='" + packageName + "'";
        String[] whereArgs = null;// new String[]{newVolume.getPackageName()};
        int flag = resolver.delete(PackageVolumeProvider.CONTENT_URI, where, whereArgs);
        if (flag == -1)
            return false;
        return true;
    }

    private Volume getVolumeByCursor(Cursor cursor) {
        Volume volume = new Volume();
        volume.setId(cursor.getInt(cursor.getColumnIndex(Column._id)));
        int flag = cursor.getColumnIndex(Column._PackageName);
        volume.setPackageName(cursor.getString(flag));
        int[] values = new int[Column.count];
        for (int i = 0; i < Column.count; i++) {
            values[i] = cursor.getInt(flag + 1 + i);
        }
        volume.setFollowSystem(cursor.getInt(cursor.getColumnIndex(Column._Follow_System)) == 0 ? false : true);
        volume.setValues(values);
        return volume;
    }
}
