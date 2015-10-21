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

        if (synchronization.getInstallReferrer()) {
            synchronizeWithInstallReferrer(synchronization);
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

    protected void synchronizeWithInstallReferrer(final Synchronization synchronization) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String installReferrer = GrowthLink.getInstance().waitInstallReferrer(INSTALLREFERRER_TIMEOUT);
                Synchronization.save(synchronization);

                if (installReferrer == null || installReferrer.length() == 0)
                    return;

                final String uriString = "?" + installReferrer.replace("growthlink.clickId", "clickId").replace("growthbeat.uuid", "uuid");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));
                    }
                });
            }
        }).start();

    }

    protected void synchronizeWithCookieTracking(final Synchronization synchronization) {

        String advertisingId = null;
        try {
            advertisingId = DeviceUtils.getAdvertisingId().get();
        } catch (Exception e) {
            GrowthLink.getInstance().getLogger().warning("Failed to get advertisingId: " + e.getMessage());
        }

        String urlString = GrowthLink.getInstance().getSyncronizationUrl() + "?applicationId="
            + GrowthLink.getInstance().getApplicationId();
        if (advertisingId != null)
            urlString += "&advertisingId=" + advertisingId;

        Synchronization.save(synchronization);

        openBrowser(urlString);

    }

    protected void synchronizeWithDeviceFingerprint(final Synchronization synchronization) {

        Synchronization.save(synchronization);

        if (synchronization.getClickId() == null)
            return;

        final String uriString = "?clickId=" + synchronization.getClickId();
        GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));

    }

    protected void openBrowser(final String urlString) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {

                Uri uri = Uri.parse(urlString);
                android.content.Intent androidIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
                androidIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                GrowthLink.getInstance().getContext().startActivity(androidIntent);

            }
        });

    }

}
