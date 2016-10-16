package com.jianghongkui.volumemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by pc on 16-10-16.
 */

public class Notice implements Parcelable {
    public final static int CHANGE_VOLUME = 1;
    public final static int SAVE_VOLUME = 2;

    public  final  static String KEY="Notice";
    private int type;
    private String name;
    private ArrayList<Integer> integers;
    public static final Parcelable.Creator<Notice> CREATOR = new Parcelable.Creator<Notice>() {
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    public Notice() {
    }

    public Notice(Parcel in) {
        type = in.readInt();
        name = in.readString();
        integers = new ArrayList<>(2);
        in.readList(integers, getClass().getClassLoader());
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getIntegers() {
        return integers;
    }

    public void setIntegers(ArrayList<Integer> integers) {
        this.integers = integers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(name);
        dest.writeList(integers);
    }
}
