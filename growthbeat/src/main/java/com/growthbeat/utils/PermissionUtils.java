package com.growthbeat.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PermissionUtils {

    public static boolean permitted(Context context, String permission) {

        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            return false;
        }

        String[] requestedPermissions = packageInfo.requestedPermissions;

        for (String requestedPermission : requestedPermissions)
            if (requestedPermission.equals(permission))
                return true;

        return false;

    }

}
