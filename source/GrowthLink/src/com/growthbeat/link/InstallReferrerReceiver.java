package com.growthbeat.link;

import com.growthbeat.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReferrerReceiver extends BroadcastReceiver {
	private static final String LOGGER_DEFAULT_TAG = "InstallReferrerReceiver";
	private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
	@Override
	public void onReceive(Context context, Intent intent) {
		logger.info("InstallReferrerReceiver onReceive called");
		GrowthLink.getInstance().getInstallReferrerReceiveHandler().onReceive(context, intent);
	}
}
