package com.growthbeat.link.callback;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.link.GrowthLink;
import com.growthbeat.link.model.Synchronization;
import com.growthbeat.utils.DeviceUtils;

public class DefaultSynchronizationCallback implements SynchronizationCallback {
	private static long INSTALLREFERRER_TIMEOUT = 60 * 1000;

	@Override
	public void onComplete(final Synchronization synchronization) {
		
		if (synchronization == null) 
			return;

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (synchronization.getInstallReferrer()) {
					String installReferrer = GrowthLink.getInstance().waitInstallReferrer(INSTALLREFERRER_TIMEOUT);
					synchronizeWithInstallReferrer(synchronization, installReferrer);
					return;
				}
				
				if (synchronization.getCookieTracking()) {
					synchronizeWithCookieTracking(synchronization);
					return;
				}
				
				if (synchronization.getDeviceFingerprint()) {
					synchronizeWithDeviceFingerprint(synchronization);
					return;
				}
				
			}
		}).start();

	}

	protected boolean synchronizeWithInstallReferrer(final Synchronization synchronization, String installReferrer) {

		if (installReferrer == null || installReferrer.length() == 0)
			return false;

		final String uriString = "?" + installReferrer.replace("growthlink.clickId", "clickId").replace("growthbeat.uuid", "uuid");
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));
				Synchronization.save(synchronization);
			}
		});

		return true;

	}

	protected void synchronizeWithCookieTracking(final Synchronization synchronization) {

		String advertisingId = null;
		try {
			advertisingId = DeviceUtils.getAdvertisingId().get();
		} catch (Exception e) {
			GrowthLink.getInstance().getLogger().warning("Failed to get advertisingId: " + e.getMessage());
		}

		final String urlString = GrowthLink.getInstance().getSyncronizationUrl() + "?applicationId="
				+ GrowthLink.getInstance().getApplicationId() + (advertisingId != null ? "&advertisingId=" + advertisingId : "");
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				openBrowser(urlString);
				Synchronization.save(synchronization);
			}
		});

	}
	
	protected void synchronizeWithDeviceFingerprint(final Synchronization synchronization) {
		if(synchronization.getClickId() == null)
			return;
		
		final String uriString = "?clickId=" + synchronization.getClickId();
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));
				Synchronization.save(synchronization);
			}
		});
	}

	protected void openBrowser(String urlString) {

		Uri uri = Uri.parse(urlString);
		android.content.Intent androidIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
		androidIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		GrowthLink.getInstance().getContext().startActivity(androidIntent);

	}
	

}
