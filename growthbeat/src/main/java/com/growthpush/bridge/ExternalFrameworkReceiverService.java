package com.growthpush.bridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.growthpush.GrowthPush;
import com.growthpush.ReceiverService;
import com.growthpush.handler.DefaultReceiveHandler;

public class ExternalFrameworkReceiverService extends ReceiverService {

    protected ExternalFrameworkBridge bridge = null;

    public ExternalFrameworkReceiverService() {
        super();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
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

        super.onMessageReceived(from, data);
    }
}
