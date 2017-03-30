package com.growthpush;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class ReceiverService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
        GrowthPush.getInstance().getLogger().info("onMessageReceived: " + "from=" + from);
        GrowthPush.getInstance().getLogger().info("onMessageReceived: " + "data=" + data.toString());
        Intent intent = new Intent();
        intent.putExtras(data);
        GrowthPush.getInstance().getReceiveHandler().onReceive(getApplicationContext(), intent);
    }
}
