package com.growthbeat.link;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReferrerReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		GrowthLink.getInstance().getLogger().info("InstallReferrerReceiver onReceive called");
		GrowthLink.getInstance().getInstallReferrerReceiveHandler().onReceive(context, intent);
	}
}
