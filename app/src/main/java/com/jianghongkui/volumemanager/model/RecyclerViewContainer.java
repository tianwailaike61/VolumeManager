package com.jianghongkui.volumemanager.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.animation.OvershootInterpolator;

import com.jianghongkui.volumemanager.other.Application;
import com.jianghongkui.volumemanager.util.MLog;
import com.jianghongkui.volumemanager.util.Utils;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by jianghongkui on 2016/9/16.
 */
public class RecyclerViewContainer<T> {

    private final static String TAG = "RecyclerViewContainer";

    private Context context;

    private RecyclerView recyclerView;
    private ProgramAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Listener listener;

    public final int REFRESH = 1;
    public final int LOADMORE = 2;

    private ArrayList<T> datas;


    public interface Listener<T> {
        ArrayList<T> refresh();

        ArrayList<T> loadmore();

        void onItemClick(int position);
    }

    public RecyclerViewContainer(Context context, RecyclerView recyclerView) {
        this(context, recyclerView, null);
    }

    public RecyclerViewContainer(Context context, RecyclerView recyclerView, Listener listener) {
        this(context, recyclerView, listener, new LinearLayoutManager(context));
    }

    public RecyclerViewContainer(Context context, RecyclerView recyclerView, Listener listener, RecyclerView.LayoutManager layoutManager) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.datas = new ArrayList<>();
        this.listener = listener;
        this.layoutManager = layoutManager;
        this.adapter = getAdapter(this.datas, this.layoutManager);
        setAdapter();
    }

    private void setAdapter() {
        // 设置LinearLayoutManager
        recyclerView.setLayoutManager(layoutManager);
        // 设置ItemAnimator
        BaseItemAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        animator.setInterpolator(new OvershootInterpolator());
        animator.setAddDuration(1000);
        animator.setChangeDuration(1000);
        animator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(animator);
        // 设置固定大小
        recyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        adapter = getAdapter(datas, layoutManager);
        adapter.setListener(listener);
        // 为mRecyclerView设置适配器
        recyclerView.setAdapter(adapter);
    }

    private ProgramAdapter getAdapter(ArrayList<T> datas, RecyclerView.LayoutManager layoutManager) {
        ProgramAdapter adapter = new ProgramAdapter(context);
        if (datas != null) {
            adapter.setDatas(datas);
        }
        //adapter.setLayoutManager(layoutManager);
        //recyclerView.swapAdapter(adapter, false);
        return adapter;
    }

    public void needShowFooter(boolean isNeed) {
        adapter.setFooterEnable(isNeed);
        adapter.notifyDataSetChanged();
    }

    public boolean isFooterEnable() {
        return adapter.isFooterEnable();
    }

    public void startOperation(final Handler handler, final int OperationType) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MLog.d(TAG, "start operation in thread,the operation type is " + OperationType);
                Message message = Message.obtain();
                if (OperationType == REFRESH && listener != null) {
                    message.what = RecyclerVIewController.REFRESHING;
                    refreshing();
                }
                if (OperationType == LOADMORE && listener != null) {
                    message.what = RecyclerVIewController.LOADING;
                    loading();
                }
                handler.sendMessage(message);
            }
        }, 2000);
    }

    private void refreshing() {
        datas.clear();
        ArrayList<T> newDatas = listener.refresh();
        if (newDatas != null && newDatas.size() != 0) {
            MLog.d(TAG, "refreshing data,the size is " + newDatas.size());
            datas = newDatas;
        }
        adapter.setDatas(datas);
    }

    private void loading() {
        ArrayList<T> newDatas = listener.loadmore();
        if (newDatas != null && newDatas.size() != 0) {
            MLog.d(TAG, "refreshing data,the size is " + newDatas.size());
            datas.addAll(newDatas);
        }
        adapter.setDatas(datas);
    }

    public void addScroller(RecyclerView.OnScrollListener listener) {
        recyclerView.addOnScrollListener(listener);
    }


    public void hideFooter() {
        adapter.hideFooter();
    }

    public ArrayList<T> getData() {
        return datas;
    }


    public T getDataByPosition(int position) {
        return datas.get(position);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return recyclerView.getLayoutManager();
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        recyclerView.swapAdapter(adapter, false);
        setAdapter();
        adapter.notifyDataSetChanged();
    }


    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    public int getFirstVisiblePosition() {
        int position;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = staggeredGridLayoutManager.findFirstVisibleItemPositions(new int[staggeredGridLayoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {
        int position;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(new int[staggeredGridLayoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

}
