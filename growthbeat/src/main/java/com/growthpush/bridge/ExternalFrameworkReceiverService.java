package com.growthpush.bridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.growthpush.GrowthPush;
import com.growthpush.handler.DefaultReceiveHandler;

import java.util.Map;

public class ExternalFrameworkReceiverService extends FirebaseMessagingService {

    protected ExternalFrameworkBridge bridge = null;

    public ExternalFrameworkReceiverService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        Intent intent = new Intent();
        intent.putExtras(bundle);
        if (GrowthPush.getInstance().getReceiveHandler() != null
            && GrowthPush.getInstance().getReceiveHandler() instanceof DefaultReceiveHandler) {
            DefaultReceiveHandler receiveHandler = (DefaultReceiveHandler) GrowthPush.getInstance().getReceiveHandler();
            receiveHandler.setCallback(new DefaultReceiveHandler.Callback() {
                @Override
                public void onOpen(Context context, Intent intent) {
                    super.onOpen(context, intent);
                    if (intent != null && intent.getExtras() != null && bridge != null) {
                        bridge.callbackExternalFrameworkWithExtra(intent.getExtras());
                    }
                }
            });
        }
        GrowthPush.getInstance().getReceiveHandler().onReceive(getApplicationContext(), intent);
    }
}
