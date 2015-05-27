package com.growthbeat;

import android.content.Context;
import android.os.Build;

import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.message.GrowthMessage;

public class Growthbeat {

	private static final Growthbeat instance = new Growthbeat();

	private Growthbeat() {
		super();
	}

	public static Growthbeat getInstance() {
		return instance;
	}

	public void initialize(Context context, String applicationId, String credentialId) {
		context = context.getApplicationContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
			GrowthAnalytics.getInstance().initialize(context, applicationId, credentialId);
			GrowthMessage.getInstance().initialize(context, applicationId, credentialId);
		}
	}

	public void start() {
		GrowthAnalytics.getInstance().open();
	}

	public void stop() {
		GrowthAnalytics.getInstance().close();
	}

	public void setLoggerSilent(boolean silent) {
		GrowthbeatCore.getInstance().getLogger().setSilent(silent);
		GrowthAnalytics.getInstance().getLogger().setSilent(silent);
		GrowthMessage.getInstance().getLogger().setSilent(silent);
	}

}
