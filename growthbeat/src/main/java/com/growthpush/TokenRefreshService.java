package com.growthpush;

import com.google.firebase.iid.FirebaseInstanceId;

public class TokenRefreshService  {
    @Deprecated
    public void onTokenRefresh() {
        // deprecated from v2.0.11 please use ReceiverService#onNewToken()
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        GrowthPush.getInstance().getLogger().info("FCM registration token was refresh");
        GrowthPush.getInstance().registerClient(refreshedToken);
    }
}
