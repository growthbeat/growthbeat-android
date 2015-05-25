package com.growthpush.handler;

import android.content.Context;
import android.content.Intent;

/**
 * 2013/08/23
 * 
 * @author Ogawa Shigeru
 * 
 */
public class DefaultReceiveHandler extends BaseReceiveHandler {

	public DefaultReceiveHandler() {
		super();
	}

	public DefaultReceiveHandler(DefaultReceiveHandler.Callback callback) {
		this();
		setCallback(callback);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);
		showAlert(context, intent);
		addNotification(context, intent);

	}

}
