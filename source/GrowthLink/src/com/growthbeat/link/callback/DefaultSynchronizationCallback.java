package com.growthbeat.link.callback;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.link.GrowthLink;
import com.growthbeat.link.model.Synchronization;
import com.growthbeat.utils.DeviceUtils;

public class DefaultSynchronizationCallback implements SynchronizationCallback {

	@Override
	public void onComplete(Synchronization synchronization) {

		if ((GrowthLink.getInstance().getInstallReferrer() != null) || !synchronization.getBrowser())
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {

				String urlString = GrowthLink.getInstance().getSyncronizationUrl() + "?applicationId="
						+ GrowthLink.getInstance().getApplicationId();
				try {
					String advertisingId = DeviceUtils.getAdvertisingId().get();
					if (advertisingId != null) {
						urlString += "&advertisingId=" + advertisingId;
					}
				} catch (Exception e) {
					GrowthLink.getInstance().getLogger().warning("Failed to get advertisingId: " + e.getMessage());
				}

				Uri uri = Uri.parse(urlString);
				final android.content.Intent androidIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
				androidIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					public void run() {
						GrowthLink.getInstance().getContext().startActivity(androidIntent);
					}
				});

			}
		}).start();

	}

}
