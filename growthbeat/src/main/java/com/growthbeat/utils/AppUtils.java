package com.growthbeat.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import androidx.core.content.pm.PackageInfoCompat;

public final class AppUtils {

    public static String getaAppVersion(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }

    }

    public static String getAppBuild(Context context) {

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            long longVersionCode = PackageInfoCompat.getLongVersionCode(packageInfo);
            return String.valueOf((int) longVersionCode);
        } catch (NameNotFoundException e) {
            return null;
        }

    }

}
