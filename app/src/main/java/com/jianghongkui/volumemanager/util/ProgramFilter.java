package com.jianghongkui.volumemanager.util;

import android.widget.Filter;
import android.widget.Filterable;

/**
 * Created by jianghongkui on 2016/9/15.
 */
public class ProgramFilter implements Filterable {

    public  String filter(String packageName){
        return "";
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
