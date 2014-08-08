package com.growthbeat;

import android.content.Context;

import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;
import com.growthreplay.GrowthReplay;

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

	public void initializeGrowthPush(Environment environment, String senderId) {
		GrowthPush.getInstance().initialize(context, applicationId, credentialId, environment, senderId);
	}

	public void intializeGrowthReplay() {
		GrowthReplay.getInstance().initialize(context, applicationId, credentialId);
	}

}
