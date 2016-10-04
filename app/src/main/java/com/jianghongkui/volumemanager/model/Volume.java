package com.jianghongkui.volumemanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by jianghongkui on 2016/9/18.
 */
public class Volume implements Parcelable {
    private int id = -1;
    private String packageName;
    private boolean isFollowSystem = true;
//    private int voice_call;
//    private int system;
//    private int ring;
//    private int music;
//    private int alarm;
//    private int notification;
//    private int bluetooth_call;
//    private int system_enforced;
//    private int DTMF;
//    private int TTS;

    private int[] values;

    public Volume() {
        values = new int[Column.count];
    }

    protected Volume(Parcel in) {
        id = in.readInt();
        packageName = in.readString();
        isFollowSystem = in.readByte() != 0;
        values = in.createIntArray();
    }

    public static final Creator<Volume> CREATOR = new Creator<Volume>() {
        @Override
        public Volume createFromParcel(Parcel in) {
            return new Volume(in);
        }

        @Override
        public Volume[] newArray(int size) {
            return new Volume[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public int[] getValues() {
        return values;
    }

    public void setFollowSystem(boolean followSystem) {
        isFollowSystem = followSystem;
    }

    public boolean isFollowSystem() {
        return isFollowSystem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Volume volume = (Volume) o;

        if (isFollowSystem != volume.isFollowSystem) return false;
        if (packageName != null ? !packageName.equals(volume.packageName) : volume.packageName != null)
            return false;
        return Arrays.equals(values, volume.values);
    }

    public static int[] getDifferentValueType(Volume volume1, Volume volume2) {
        int[] diff = new int[Column.count];
        int cout = 0;
        for (int i = 0; i < Column.count; i++) {
            if (volume1.getValue(i) != volume2.getValue(i)) {
                diff[cout++] = i;
            }
        }
        return diff;
    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (isFollowSystem ? 1 : 0);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    public static Volume clone(Volume volume) {
        Volume newVolume = new Volume();
        newVolume.setId(volume.getId());
        newVolume.setPackageName(volume.getPackageName());
        newVolume.setValues(volume.getValues());
        return newVolume;
    }

    public int getValue(int count) {
        return values[count];
    }

    public void setValue(int count, int value) {
        values[count] = value;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        if (id != -1)
            contentValues.put(Column._id, id);
        contentValues.put(Column._PackageName, packageName);
        contentValues.put(Column._Voice_Call, values[0]);
        contentValues.put(Column._System, values[1]);
        contentValues.put(Column._Ring, values[2]);
        contentValues.put(Column._Music, values[3]);
        contentValues.put(Column._Alarm, values[4]);
        contentValues.put(Column._Notification, values[5]);
//        contentValues.put(Column._Bluetooth_Call, values[6]);
//        contentValues.put(Column._System_Enforced, values[7]);
//        contentValues.put(Column._DTMF, values[8]);
//        contentValues.put(Column._TTS, values[9]);
        contentValues.put(Column._Follow_System, isFollowSystem ? 1 : 0);
        return contentValues;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(packageName);
        dest.writeByte((byte) (isFollowSystem ? 1 : 0));
        dest.writeIntArray(values);
    }
}
