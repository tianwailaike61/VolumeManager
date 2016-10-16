package com.jianghongkui.volumemanager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jianghongkui.volumemanager.model.Program;
import com.jianghongkui.volumemanager.model.RecyclerVIewController;
import com.jianghongkui.volumemanager.model.RecyclerViewContainer;
import com.jianghongkui.volumemanager.other.WindowChangeDetectingService;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.MPackageManager;
import com.jianghongkui.volumemanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VolumeActivity extends AppCompatActivity implements RecyclerViewContainer.Listener<Program> {

    private final static String TAG = "VolumeActivity";

    @BindView(R.id.programs)
    RecyclerView programs;
    @BindView(R.id.programs_refresh_layout)
    SwipeRefreshLayout programsRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.parentPanel)
    CoordinatorLayout parentPanel;

    private RecyclerVIewController recyclerVIewController;

    private List<PackageInfo> packageInfos;

    private VolumeDetailsFragement volumeDetailsFragement;
    private AboutUsFragment aboutUsFragment;

    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);
        ButterKnife.bind(this);
        activity = this;
        setSupportActionBar(toolbar);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Utils.isAccessibilitySettingsOn(activity, WindowChangeDetectingService.Service)) {
            needSetting();
        }
    }

    public void needSetting() {
        final Snackbar snackBar = Snackbar.make(parentPanel, R.string.snackbar_messege, Snackbar.LENGTH_INDEFINITE);
        //设置SnackBar背景颜色
        snackBar.getView().setBackgroundColor(Color.GRAY);
        //设置按钮文字颜色
        snackBar.setActionTextColor(Color.RED);
        //设置点击事件
        snackBar.setAction(R.string.snackbar_setting, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                snackBar.dismiss();
            }
        }).show();
//        //设置回调
//        snackBar.setCallback(new Snackbar.Callback() {
//
//            @Override
//            public void onDismissed(Snackbar snackbar, int event) {
//                super.onDismissed(snackbar, event);
//            }
//
//
//            @Override
//            public void onShown(Snackbar snackbar) {
//                super.onShown(snackbar);
//                Toast.makeText(activity, "Snackbar show", Toast.LENGTH_SHORT).show();
//            }
//        }).show();
    }

    public void init() {
        RecyclerViewContainer<Program> applicationRecyclerViewContainer = new RecyclerViewContainer(this, programs, this);
        recyclerVIewController = new RecyclerVIewController(applicationRecyclerViewContainer, programsRefreshLayout);
        recyclerVIewController.firstRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_volume, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(activity, SettingsActivity.class));
                break;
//            case R.id.change_recycler_layoutmanager:
//                RecyclerView.LayoutManager layoutManager = recyclerVIewController.getLayoutManager();
//                RecyclerView.LayoutManager newLayoutManager;
//                if (layoutManager instanceof GridLayoutManager) {
//                    MLog.d(TAG, "this layoutmanager is GridLayoutManager");
//                    newLayoutManager = new LinearLayoutManager(activity);
//                } else if (layoutManager instanceof LinearLayoutManager) {
//                    MLog.d(TAG, "this layoutmanager is LinearLayoutManager");
//                    newLayoutManager = new GridLayoutManager(activity, 4);
//                } else {
//                    newLayoutManager = layoutManager;
//                }
//                recyclerVIewController.switchLayoutManager(newLayoutManager);
//                break;
            case R.id.menu_about_us:
                if (aboutUsFragment == null)
                    aboutUsFragment = new AboutUsFragment();
                aboutUsFragment.show(getSupportFragmentManager(), "about us");
                break;
        }
        return true;
    }

    private Program getApplication(PackageInfo packageInfo) {
        int iconWidth, iconHeigh;
        iconWidth = iconHeigh = 100;//(int) getResources().getDimension(R.dimen.recyclerview_icon_size);
        Program application = new Program();
        application.setName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
        Drawable drawable = packageInfo.applicationInfo.loadIcon(getPackageManager());
        application.setIcon(Utils.drawableToBitamp(drawable, iconWidth, iconHeigh));
        application.setPackageName(packageInfo.packageName);
        return application;
    }


    @Override
    public ArrayList<Program> refresh() {
        MLog.d(TAG, "refresh");
        ArrayList<Program> data = new ArrayList<>();
        packageInfos = MPackageManager.newInstance(this).getPackageInfos();
        int size = packageInfos.size();
        MLog.d(TAG, "total size:" + size);
        recyclerVIewController.setTotal(size);
        int count = recyclerVIewController.needAddCount();
        MLog.d(TAG, "it will add " + count + " datas");
        for (int i = 0; i < count; i++) {
            data.add(getApplication(packageInfos.get(i)));
        }
        recyclerVIewController.setCount(count);
        return data;
    }

    @Override
    public ArrayList<Program> loadmore() {
        MLog.d(TAG, "loadmore");
        ArrayList<Program> data = new ArrayList<>();
        int currentCount = recyclerVIewController.getCount();
        int needCount = recyclerVIewController.needAddCount();
        for (int i = 0; i < needCount; i++) {
            data.add(getApplication(packageInfos.get(currentCount + i)));
        }
        recyclerVIewController.setCount(currentCount + needCount);
        return data;
    }

    @Override
    public void onItemClick(int position) {
        MLog.d(TAG, "onItemClick position:" + position);
        if (volumeDetailsFragement == null || !volumeDetailsFragement.isVisibility()) {
            volumeDetailsFragement = new VolumeDetailsFragement();
            volumeDetailsFragement.setApplication(recyclerVIewController.getDataByPosition(position));
            volumeDetailsFragement.show(getSupportFragmentManager(), "dialog");
            volumeDetailsFragement.setVisibility(true);
        }
    }

}
