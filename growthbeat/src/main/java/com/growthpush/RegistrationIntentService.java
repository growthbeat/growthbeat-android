package com.growthpush;

import android.app.IntentService;
import android.content.Intent;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = GrowthPush.getInstance().registerFCM();
        if (token != null) {
            GrowthPush.getInstance().getLogger().info("FCM registration token: " + token);
            GrowthPush.getInstance().registerClient(token);
        }
    }
}
