package com.growthbeat;

import android.content.Context;
import android.os.Build;

import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.message.GrowthMessage;

public class Growthbeat {

	private static final Growthbeat instance = new Growthbeat();

	private Context context;
	private String applicationId;
	private String credentialId;

	private Growthbeat() {
		super();
	}

	public static Growthbeat getInstance() {
		return instance;
	}

	public void initialize(Context context, String applicationId, String credentialId) {
		this.context = context.getApplicationContext();
		this.applicationId = applicationId;
		this.credentialId = credentialId;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
		}
	}

	public void initializeGrowthAnalytics() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			GrowthAnalytics.getInstance().initialize(context, applicationId, credentialId);
		}
	}

	public void initializeGrowthMessage() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
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
