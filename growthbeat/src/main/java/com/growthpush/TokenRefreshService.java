package com.growthpush;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class TokenRefreshService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        GrowthPush.getInstance().getLogger().info("FCM registration token was refresh");
        GrowthPush.getInstance().registerClient(refreshedToken);
    }
}
