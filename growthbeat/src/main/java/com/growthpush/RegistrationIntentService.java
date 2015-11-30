package com.growthpush;

import android.app.IntentService;
import android.content.Intent;

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String token = GCMRegister.registerSync(this);
        if (token != null) {
            GrowthPush.getInstance().getLogger().info("GCM registration token: " + token);
            GrowthPush.getInstance().registerClient(token);
        }
    }
}
