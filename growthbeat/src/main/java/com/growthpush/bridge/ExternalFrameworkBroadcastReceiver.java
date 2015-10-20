package com.growthpush.bridge;

import android.content.Context;
import android.content.Intent;

import com.growthpush.BroadcastReceiver;
import com.growthpush.GrowthPush;
import com.growthpush.handler.DefaultReceiveHandler;

public abstract class ExternalFrameworkBroadcastReceiver extends BroadcastReceiver {

	protected ExternalFrameworkBridge bridge = null;

	public ExternalFrameworkBroadcastReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

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

		super.onReceive(context, intent);
	}

}
