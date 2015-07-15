package com.growthbeat.link;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallReceiver extends BroadcastReceiver  {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GrowthLink.getInstance().getInstallReceiveHandler().onReceive(context, intent);
	}
}
