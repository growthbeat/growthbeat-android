package com.growthpush;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Shigeru Ogawa on 13/08/09.
 */
public class BroadcastReceiver extends android.content.BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			handleReceive(context, intent);
		}

	}

	private void handleRegistration(Context context, Intent intent) {

		if (intent.getExtras().containsKey("error"))
			GrowthPush.getInstance().getLogger().error(String.format("GCM Registration failed. %s", intent.getExtras().getString("error")));

		if (intent.getExtras().containsKey("registration_id"))
			GrowthPush.getInstance().registerClient(intent.getExtras().getString("registration_id"));

	}

	private void handleReceive(Context context, Intent intent) {

		String messageType = GoogleCloudMessaging.getInstance(context).getMessageType(intent);
		if (messageType != null && messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE))
			GrowthPush.getInstance().getReceiveHandler().onReceive(context, intent);

	}

}
