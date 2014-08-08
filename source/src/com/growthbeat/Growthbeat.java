package com.growthbeat;

import android.content.Context;

import com.growthbeat.model.Client;
import com.growthbeat.observer.ClientObserver;
import com.growthpush.GrowthPush;

public class Growthbeat {

	public static void initialize(final Context context, final String applicationId, final String credentialId) {

		GrowthbeatCore.getInstance().addClientObserver(new ClientObserver() {
			@Override
			public void update(Client client) {
				// TODO migrate to new API
				GrowthPush.getInstance().initialize(context, 0, credentialId);
			}
		});
		GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);

	}

}
