package com.jianghongkui.volumemanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jianghongkui.volumemanager.model.Program;
import com.jianghongkui.volumemanager.model.VolumeContainer;
import com.jianghongkui.volumemanager.model.VolumeController;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianghongkui on 2016/9/18.
 */
public class VolumeDetailsFragement extends DialogFragment {
    private final static String TAG = "VolumeDetailsFragement";
    @BindView(R.id.follow_system)
    AppCompatCheckBox followSystem;
    @BindView(R.id.volumegroup)
    LinearLayout volumegroup;
    @BindView(R.id.view_program_icon)
    ImageView viewProgramIcon;
    @BindView(R.id.view_program_name)
    TextView viewProgramName;

    private Context context;

    private View view;

    private Program application;

    private VolumeController controller;

    private boolean visibility;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        visibility = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        VolumeContainer volumeContainer = new VolumeContainer(volumegroup, followSystem);
        controller = new VolumeController(context, volumeContainer, application.getPackageName());
        viewProgramIcon.setImageBitmap(application.getIcon());
        viewProgramName.setText(application.getName());
        //controller.setPackageName(application.getPackageName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.container_volume, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        Dialog dialog = builder.create();
        // 设置宽度为屏宽, 靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        window.setAttributes(lp);
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        visibility = false;
        controller.maybeSaveChanges();
        controller.restoreVolume();
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setApplication(Program application) {
        this.application = application;
    }


}
