package com.growthpush;

import com.google.android.gms.iid.InstanceIDListenerService;

import android.content.Intent;

public class TokenRefreshService extends InstanceIDListenerService {
	@Override
	public void onTokenRefresh() {
		super.onTokenRefresh();

		GrowthPush.getInstance().getLogger().info("GCM registration token was refresh");
		Intent intent = new Intent(this, RegistrationIntentService.class);
		startService(intent);
	}
}
