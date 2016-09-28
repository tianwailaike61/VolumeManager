package com.jianghongkui.volumemanager.model;

import android.graphics.Bitmap;

/**
 * Created by jianghongkui on 2016/9/18.
 */
public class Program {

    private String PackageName;
    private String Name;
    private Bitmap Icon;
   // private Intent LaunchIntent;

    public Program() {
    }

    public Program(String PackageName, String name, Bitmap icon) {
        Name = name;
        Icon = icon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    public Bitmap getIcon() {
        return Icon;
    }

    public void setIcon(Bitmap icon) {
        Icon = icon;
    }

}
