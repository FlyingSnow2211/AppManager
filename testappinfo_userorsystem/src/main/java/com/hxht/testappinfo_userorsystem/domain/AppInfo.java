package com.hxht.testappinfo_userorsystem.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {

    private String appName;
    private Drawable appIcon;
    private long appSize;
    private String apkPath;
    private boolean isSystemApp;
    private boolean isInstalledPhone;
    private String packageName ;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setIsSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

    public boolean isInstalledPhone() {
        return isInstalledPhone;
    }

    public void setIsInstalledPhone(boolean isInstalledPhone) {
        this.isInstalledPhone = isInstalledPhone;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", appIcon=" + appIcon +
                ", appSize=" + appSize +
                ", apkPath='" + apkPath + '\'' +
                ", isSystemApp=" + isSystemApp +
                ", isInstalledPhone=" + isInstalledPhone +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
