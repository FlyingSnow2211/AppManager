package com.hxht.testappinfo_userorsystem.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.hxht.testappinfo_userorsystem.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppInfoParser {

    public static List<AppInfo> getAllAppInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo appInfo = new AppInfo();

            String packageName = packageInfo.applicationInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String apkPath = packageInfo.applicationInfo.sourceDir;

            File file = new File(apkPath);
            long appSize = file.length();

            appInfo.setAppName(name);
            appInfo.setAppIcon(icon);
            appInfo.setPackageName(packageName);
            appInfo.setAppSize(appSize);

            int flags = packageInfo.applicationInfo.flags;
            //判断应用程序的安装位置
            if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
                //表示SD卡存储
                appInfo.setIsInstalledPhone(false);
            } else {
                //表示手机内存存储
                appInfo.setIsInstalledPhone(true);
            }

            //判断系统应用还是用户应用
            if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
                //表示是系统应用
                appInfo.setIsSystemApp(true);
            } else {
                appInfo.setIsSystemApp(false);
            }

            list.add(appInfo);
            appInfo = null;
        }

        return list;
    }
}
