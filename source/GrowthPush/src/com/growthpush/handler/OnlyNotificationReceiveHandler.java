package com.growthpush.handler;

import android.content.Context;
import android.content.Intent;

public class OnlyNotificationReceiveHandler extends BaseReceiveHandler {

	public OnlyNotificationReceiveHandler() {
		super();
	}

	public OnlyNotificationReceiveHandler(DefaultReceiveHandler.Callback callback) {
		this();
		setCallback(callback);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
		addNotification(context, intent);

	}

}
