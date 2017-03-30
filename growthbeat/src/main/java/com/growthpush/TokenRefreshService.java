package com.growthpush;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class TokenRefreshService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        GrowthPush.getInstance().getLogger().info("GCM registration token was refresh");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
