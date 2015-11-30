package com.growthpush;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GCMRegister {

    public static String registerSync(final Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_META_DATA);
            String senderId = applicationInfo.metaData.getString("com.growthpush.senderid").replace("id:", "");
            InstanceID instanceID = InstanceID.getInstance(context);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            return token;
        } catch (Exception e) {
            return null;
        }
    }
}
