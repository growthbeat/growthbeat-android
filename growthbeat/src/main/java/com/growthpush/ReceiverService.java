package com.growthpush;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ReceiverService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        GrowthPush.getInstance().getLogger().info("onMessageReceived: " + "from=" + from);
        GrowthPush.getInstance().getLogger().info("onMessageReceived: " + "data=" + data.toString());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        GrowthPush.getInstance().getReceiveHandler().onReceive(getApplicationContext(), intent);
    }

}
