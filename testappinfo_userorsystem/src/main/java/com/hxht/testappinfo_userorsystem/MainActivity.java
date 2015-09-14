package com.hxht.testappinfo_userorsystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hxht.testappinfo_userorsystem.adapter.MyRecyclerViewAdapter;
import com.hxht.testappinfo_userorsystem.domain.AppInfo;
import com.hxht.testappinfo_userorsystem.utils.AppInfoParser;
import com.hxht.testappinfo_userorsystem.utils.DensityUtil;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView recyclerView;
    private TextView tv_phone_free_memory;
    private TextView tv_sd_free_memory;
    private List<AppInfo> systemAppLists;
    private List<AppInfo> userAppLists;
    private TextView tv_appcount;
    private PopupWindow popupwindow;
    private AppInfo clickAppInfo;
    private LinearLayout ll_loading;
    private UnInstallAppBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();


        receiver = new UnInstallAppBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
    }

    /**
     * 找控件
     */
    private void initView() {
        tv_phone_free_memory = (TextView) findViewById(R.id.tv_phone_free_memory);
        tv_sd_free_memory = (TextView) findViewById(R.id.tv_sd_free_memory);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        tv_appcount = (TextView) findViewById(R.id.tv_appcount);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    /**
     * 填充数据
     */
    private void initData() {

        ll_loading.setVisibility(View.VISIBLE);

        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        long phoneFreeSpace = Environment.getDataDirectory().getFreeSpace();

        String sdFreeSpaceStr = Formatter.formatFileSize(MainActivity.this, sdFreeSpace);
        String phoneFreeSpaceStr = Formatter.formatFileSize(MainActivity.this, phoneFreeSpace);

        tv_sd_free_memory.setText("剩余SD卡内存：" + sdFreeSpaceStr);
        tv_phone_free_memory.setText("剩余手机内存：" + phoneFreeSpaceStr);

        systemAppLists = new ArrayList<>();
        systemAppLists.clear();
        userAppLists = new ArrayList<>();
        userAppLists.clear();

        List<AppInfo> allAppInfo = AppInfoParser.getAllAppInfo(MainActivity.this);
        for (AppInfo appInfo : allAppInfo) {
            boolean isSystemApp = appInfo.isSystemApp();
            if (isSystemApp) {
                systemAppLists.add(appInfo);
            } else {
                userAppLists.add(appInfo);
            }
        }

        tv_appcount.setText("用户应用：" + userAppLists.size() + "个");


        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(MainActivity.this, systemAppLists, userAppLists);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);

        tv_appcount.setVisibility(View.GONE);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dismissPopupWindow();
            }
        });

        adapter.setmOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                clickAppInfo = null;

                if (position < (userAppLists.size() + 1)) {
                    // 用户程序
                    clickAppInfo = userAppLists.get(position - 1);// 多了一个textview的标签 ，
                    // 位置需要-1
                } else {
                    // 系统程序
                    int location = position - 1 - userAppLists.size() - 1;
                    clickAppInfo = systemAppLists.get(location);
                }


                View contentView = View.inflate(getApplicationContext(),
                        R.layout.popup_item, null);
                LinearLayout ll_uninstall = (LinearLayout) contentView
                        .findViewById(R.id.ll_uninstall);
                LinearLayout ll_start = (LinearLayout) contentView
                        .findViewById(R.id.ll_start);
                LinearLayout ll_share = (LinearLayout) contentView
                        .findViewById(R.id.ll_share);
                LinearLayout ll_setting = (LinearLayout) contentView
                        .findViewById(R.id.ll_setting);

                /**
                 * 卸载----点击事件
                 */
                ll_uninstall.setOnClickListener(MainActivity.this);

                /**
                 * 启动----点击事件
                 */
                ll_start.setOnClickListener(MainActivity.this);

                /**
                 * 分享----点击事件
                 */
                ll_share.setOnClickListener(MainActivity.this);

                /**
                 * 设置----点击事件
                 */
                ll_setting.setOnClickListener(MainActivity.this);


                dismissPopupWindow();

                popupwindow = new PopupWindow(contentView, -2, -2);
                // 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
                popupwindow.setBackgroundDrawable(new ColorDrawable(
                        Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                //显示弹出气泡效果。
                //在代码里面所有的长度单位都是 像素。
                int dip = 60;
                int px = DensityUtil.dip2px(getApplicationContext(), dip);
                //把dip转化成 像素
                popupwindow.showAtLocation(view, Gravity.LEFT
                        + Gravity.TOP, px, location[1]);
                ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(200);
                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(200);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(aa);
                set.addAnimation(sa);
                contentView.startAnimation(set);
            }
        });

        ll_loading.setVisibility(View.GONE);
    }

    private void dismissPopupWindow() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            popupwindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_uninstall:
                toAppUninstall();
                break;
            case R.id.ll_setting:
                toAppSetting();
                break;
            case R.id.ll_share:
                toAppShare();
                break;
            case R.id.ll_start:
                toAppStart();
                break;
        }
    }

    /**
     * 启动应用
     */
    private void toAppStart() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(clickAppInfo.getPackageName());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "改程序没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享应用
     */
    private void toAppShare() {
        Intent intent = new Intent();
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "应用管理火爆来袭，小飞飞为您推荐最好用的应用管理应用......");
        startActivity(intent);
    }

    /**
     * App详情界面
     */
    private void toAppSetting() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
        startActivity(intent);
    }

    /**
     * 卸载应用
     */
    private void toAppUninstall() {
        if (!clickAppInfo.isSystemApp()) {
            //用户应用----直接卸载
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
            startActivity(intent);
        } else {
            //系统应用----获取Root权限----卸载应用

            /*
            if (!RootTools.isRootAvailable()) {
                Toast.makeText(this, "卸载系统应用，需要Root权限", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (!RootTools.isAccessGiven()) {
                    Toast.makeText(this, "请授予应用管理Root权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                RootTools.sendShell("mount -o remount ,rw /system", 3000);
                RootTools.sendShell("rm -r " + clickAppInfo.getApkPath(), 30000);

            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RootToolsException e) {
                e.printStackTrace();
            }

            */
            Toast.makeText(this, "系统应用，不可卸载", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class UnInstallAppBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "应用卸载完成", Toast.LENGTH_SHORT).show();
            initData();
        }
    }
}
