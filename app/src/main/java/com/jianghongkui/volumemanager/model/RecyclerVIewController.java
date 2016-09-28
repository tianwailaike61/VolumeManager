package com.jianghongkui.volumemanager.model;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.jianghongkui.volumemanager.util.MLog;


/**
 * Created by jianghongkui on 2016/9/16.
 */
public class RecyclerVIewController extends RecyclerView.OnScrollListener implements SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = "RecyclerVIewController";
    private RecyclerViewContainer container;

    public final static int REFRESHING = 1;
    public final static int NORMAL = 2;
    public final static int LOADING = 3;

    private int total;
    private int count;

    private int state;

    private SwipeRefreshLayout swipeRefreshLayout;

    public final static int ONECE_LOAD_COUNT = 20;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESHING:
                    MLog.d(TAG, "REFRESHING");
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case LOADING:
                    MLog.d(TAG, "LOADING");
                    container.hideFooter();
                    break;
            }
            state = NORMAL;
        }
    };

    public RecyclerVIewController(RecyclerViewContainer container, SwipeRefreshLayout swipeRefreshLayout) {
        this.container = container;
        container.addScroller(this);
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.swipeRefreshLayout.setOnRefreshListener(this);
        state = NORMAL;
    }

    public Program getDataByPosition(int position) {
        Program application = (Program) container.getDataByPosition(position);
        return application;
    }

    public void setTotal(int total) {
        this.total = total;
        if (total > 20) {
            container.needShowFooter(true);
        } else {
            container.needShowFooter(false);
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if (count == total) {
            container.needShowFooter(false);
        } else {
            container.needShowFooter(true);
        }
    }

    public int needAddCount() {
        if (count + ONECE_LOAD_COUNT > total)
            return total - count;
        return ONECE_LOAD_COUNT;

    }

    public void firstRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        this.onRefresh();
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return container.getLayoutManager();
    }

//    /**
//     * * 切换layoutManager
//     * 为了保证切换之后页面上还是停留在当前展示的位置，记录下切换之前的第一条展示位置，切换完成之后滚动到该位置
//     * 另外切换之后必须要重新刷新下当前已经缓存的itemView，否则会出现布局错乱（俩种模式下的item布局不同），
//     * RecyclerView提供了swapAdapter来进行切换adapter并清理老的itemView cache
//     *
//     * @param layoutManager
//     */
//    public void switchLayoutManager(RecyclerView.LayoutManager layoutManager) {
//        int firstVisiblePosition = container.getFirstVisiblePosition();
//        container.setLayoutManager(layoutManager);
//        swipeRefreshLayout.setRefreshing(true);
//        container.getLayoutManager().scrollToPosition(firstVisiblePosition);
//    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int lastVisiblePosition = container.getLastVisiblePosition();
        if (state == NORMAL && dy > 0 && lastVisiblePosition == count) {
            state = LOADING;
            container.startOperation(handler, container.LOADMORE);
        }
    }

    @Override
    public void onRefresh() {
        if (state == NORMAL) {
            container.needShowFooter(false);
            state = REFRESHING;
            container.startOperation(handler, container.REFRESH);
        }
    }
}
