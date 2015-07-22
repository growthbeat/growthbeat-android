package com.growthbeat.link;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class InstallReceiver extends BroadcastReceiver  {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String referrer = intent.getStringExtra("referrer");
		if(referrer != null) {
			String decoded = "";
			try {
				decoded = URLDecoder.decode(referrer, "utf-8");
			} catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(decoded.length() > 0) {
				SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(GrowthLink.PREFERENCES, 0);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(GrowthLink.REFERRER_KEY, decoded);
				editor.commit();
				GrowthLink.getInstance().getInstallReceiveHandler().onReceive(context, intent);
			}
		}
		GrowthLink.getInstance().getInstallReceiveHandler().onReceive(context, intent);
	}
}
