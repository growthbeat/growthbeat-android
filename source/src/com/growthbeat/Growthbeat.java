package com.growthbeat;

import android.content.Context;

import com.growthbeat.analytics.GrowthAnalytics;

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

}
