package com.hxht.testappinfo_userorsystem.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.Formatter;

import com.hxht.testappinfo_userorsystem.R;
import com.hxht.testappinfo_userorsystem.domain.AppInfo;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {

    private static final int TextViewType = 1;
    private static final int ViewType = 2;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private Context mContext;
    private List<AppInfo> systemAppList;
    private List<AppInfo> userAppList;


    public MyRecyclerViewAdapter(Context mContext, List<AppInfo> systemAppList, List<AppInfo> userAppList) {
        this.mContext = mContext;
        this.systemAppList = systemAppList;
        this.userAppList = userAppList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TextViewType:
                View view01 = View.inflate(mContext, R.layout.activity_view_item, null);
                holder = new MyTextViewHolder(view01);
                break;
            case ViewType:
                View view02 = View.inflate(mContext, R.layout.activity_rv_item, null);
                holder = new MyViewHolder(view02);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (position == 0) {
            ((MyTextViewHolder) viewHolder).tv_desc.setText("用户应用：" + userAppList.size() + "个");
        } else if (position == userAppList.size() + 1) {
            ((MyTextViewHolder) viewHolder).tv_desc.setText("系统应用：" + systemAppList.size() + "个");
        } else {
            AppInfo appInfo = null;

            if (position < (userAppList.size() + 1)) {
                // 用户程序
                appInfo = userAppList.get(position - 1);// 多了一个textview的标签 ，
                // 位置需要-1
            } else {
                // 系统程序
                int location = position - 1 - userAppList.size() - 1;
                appInfo = systemAppList.get(location);
            }

            ((MyViewHolder) viewHolder).app_icon.setImageDrawable(appInfo.getAppIcon());
            ((MyViewHolder) viewHolder).app_name.setText(appInfo.getAppName());
            ((MyViewHolder) viewHolder).app_size.setText(Formatter.formatFileSize(mContext.getApplicationContext(), appInfo.getAppSize()));

            if (appInfo.isInstalledPhone()) {
                ((MyViewHolder) viewHolder).app_position.setText("手机内存");
            } else {
                ((MyViewHolder) viewHolder).app_position.setText("SD卡内存");
            }

            ((MyViewHolder) viewHolder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return userAppList.size() + systemAppList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == userAppList.size() + 1) {
            return TextViewType;
        } else {
            return ViewType;
        }
    }

    class MyTextViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tv_desc;

        public MyTextViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView app_name;
        private TextView app_size;
        private TextView app_position;
        private ImageView app_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.app_icon = (ImageView) itemView.findViewById(R.id.app_icon);
            this.app_name = (TextView) itemView.findViewById(R.id.app_name);
            this.app_position = (TextView) itemView.findViewById(R.id.app_position);
            this.app_size = (TextView) itemView.findViewById(R.id.app_size);
        }
    }
}
