package com.growthpush;

public class TokenRefreshService  {
    @Deprecated
    public void onTokenRefresh() {
        // deprecated from v2.0.11 please use ReceiverService#onNewToken()
        GrowthPush.getInstance().getLogger().info("TokenRefreshService.onTokenRefresh deprecated, use ReceiverService");
    }
}
