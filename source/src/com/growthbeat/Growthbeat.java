package com.growthbeat;

import android.content.Context;

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
		this.context = context;
		this.applicationId = applicationId;
		this.credentialId = credentialId;
		GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
	}

	public void initializeGrowthAnalytics() {
		GrowthAnalytics.getInstance().initialize(context, applicationId, credentialId);
	}

	public void initializeGrowthMessage() {
		GrowthMessage.getInstance().initialize(context, applicationId, credentialId);
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
