package com.jianghongkui.volumemanager.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jianghongkui.volumemanager.R;
import com.jianghongkui.volumemanager.util.MLog;

import java.util.ArrayList;

/**
 * Created by jianghongkui on 2016/9/15.
 */
public class ProgramAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ProgramAdapter";
    protected ArrayList<Program> datas;
    protected Context context;

    protected final int TYPE_NORMAL = 0;
    protected final int TYPE_FOOTER = 1;

    protected boolean isFooterEnable = false;

    protected RecyclerView.LayoutManager layoutManager;


    protected RecyclerViewContainer.Listener listener;

    public ProgramAdapter(Context context) {
        this.datas = new ArrayList<>();
        this.context = context;
        isFooterEnable = false;
    }

    public ProgramAdapter(Context context, ArrayList datas) {
        this(context);
        this.datas = datas;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterHolder(LayoutInflater.from(context).inflate(R.layout.item_foot, parent, false));
        }
        View v = LayoutInflater.from(context).inflate(R.layout.item_program_horizontal, parent, false);
        if (layoutManager instanceof GridLayoutManager) {
            MLog.e(TAG, "the layout manager is GridLayoutManager");
            v = LayoutInflater.from(context).inflate(R.layout.item_program_vertical, parent, false);
        } else if (layoutManager instanceof LinearLayoutManager) {
            MLog.e(TAG, "the layout manager is LinearLayoutManager");
            v = LayoutInflater.from(context).inflate(R.layout.item_program_horizontal, parent, false);
        }
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            ItemHolder itemHolder = (ItemHolder) holder;
            Program application = datas.get(position);
            itemHolder.setName(application.getName());
            itemHolder.setIcon(application.getIcon());

        } else if (holder instanceof FooterHolder) {
            FooterHolder footHolder = (FooterHolder) holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int footPosition = getItemCount() - 1;
        if (footPosition == position && isFooterEnable) {
            return TYPE_FOOTER;
        }
//        /**
//         * 这么做保证layoutManager切换之后能及时的刷新上对的布局
//         * */
//        if (layoutManager == null || layoutManager instanceof LinearLayoutManager) {
//            return TYPE_LIST;
//        } else if (layoutManager instanceof GridLayoutManager) {
//            return TYPE_STAGGER;
//        }
        return TYPE_NORMAL;

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (datas != null) {
            count = datas.size();
            if (isFooterEnable) count = count + 1;
        }
        return count;
    }

//    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
//        this.layoutManager = layoutManager;
//    }

    public void setListener(RecyclerViewContainer.Listener listener) {
        this.listener = listener;
    }

    public boolean isFooterEnable() {
        return isFooterEnable;
    }

    public void setFooterEnable(boolean footerEnable) {
        isFooterEnable = footerEnable;
    }

    public void hideFooter() {
        notifyItemRemoved(getItemCount() - 1);
    }
    public void setDatas(ArrayList applications){
        datas.clear();
        datas.addAll(applications);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;

        public ItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.view_program_name);
            icon = (ImageView) itemView.findViewById(R.id.view_program_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(ProgramAdapter.ItemHolder.this.getLayoutPosition());
                    }
                }
            });
        }

        public void setName(String nameStr) {
            this.name.setText(nameStr);
        }

        public void setIcon(Bitmap background) {
            this.icon.setImageBitmap(background);
        }

    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        ViewGroup viewGroup;

        public FooterHolder(View itemView) {
            super(itemView);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.recyclerview_foot);
        }
    }

}
