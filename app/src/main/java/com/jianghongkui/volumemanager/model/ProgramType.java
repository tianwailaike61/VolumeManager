package com.jianghongkui.volumemanager.model;

import com.jianghongkui.volumemanager.util.ProgramFilter;

/**
 * Created by jianghongkui on 2016/9/15.
 */
public class ProgramType {

    private String type;
    private ProgramFilter filter;
    private int[] volume_type_changes;

    public ProgramType() {
        this.type = "";
        this.filter = null;
        this.volume_type_changes = null;
    }

    public ProgramType(String type, ProgramFilter filter, int[] volume_type_changes) {
        this.type = type;
        this.filter = filter;
        this.volume_type_changes = volume_type_changes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProgramFilter getFilter() {
        return filter;
    }

    public void setFilter(ProgramFilter filter) {
        this.filter = filter;
    }

    public int[] getVolume_type_changes() {
        return volume_type_changes;
    }

    public void setVolume_type_changes(int[] volume_type_changes) {
        this.volume_type_changes = volume_type_changes;
    }
}
