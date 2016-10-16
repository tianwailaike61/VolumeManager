package com.jianghongkui.volumemanager.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.jianghongkui.volumemanager.model.Volume;
import com.jianghongkui.volumemanager.other.Application;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pc on 16-10-7.
 */

public class MPackageManager {

    private static MPackageManager mPackageManager;

    private Context context;


    private MPackageManager(Context context) {
        this.context = context;
    }

    public synchronized static MPackageManager newInstance(Context context) {
        if (mPackageManager == null)
            mPackageManager = new MPackageManager(context);
        return mPackageManager;
    }

    public List<PackageInfo> getPackageInfos() {
        PackageManager manager = context.getPackageManager();
        ArrayList<PackageInfo> packageInfos = new ArrayList<>();
        List<String> packageNames = getVolumesFromeDatabases();
        int size = 0;
        List<PackageInfo> packageInfoList = manager.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList) {
            if (!info.packageName.equals(Application.PACKAGENAME) &&
                    manager.getLaunchIntentForPackage(info.packageName) != null) {
                if (packageNames != null && packageNames.contains(info.packageName)) {
                    packageInfos.add(size++, info);
                } else {
                    packageInfos.add(info);
                }

            }
        }
        return packageInfos;
    }

    private List<String> getVolumesFromeDatabases() {
        List<Volume> volumes = VolumeDBManager.newInstace(context).query(null);
        List<String> packagenames = null;
        if (volumes != null && volumes.size() != 0) {
            packagenames = new ArrayList<>();
            for (Volume volume : volumes) {
                packagenames.add(volume.getPackageName());
            }
        }
        return packagenames;
    }
}
