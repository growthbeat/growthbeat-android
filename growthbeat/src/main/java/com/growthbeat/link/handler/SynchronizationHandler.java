package com.growthbeat.link.handler;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.Growthbeat;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.link.model.Synchronization;
import com.growthbeat.utils.DeviceUtils;

/**
 * Created by tabatakatsutoshi on 2016/01/26.
 */
public class SynchronizationHandler {

    private static long INSTALLREFERRER_TIMEOUT = 60 * 1000;

    public void synchronizeWithInstallReferrer(final Synchronization synchronization) {
        Growthbeat.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String installReferrer = GrowthLink.getInstance().waitInstallReferrer(INSTALLREFERRER_TIMEOUT);

                if (installReferrer == null || installReferrer.length() == 0)
                    return;

                final String uriString = "?" + installReferrer.replace("growthlink.clickId", "clickId").replace("growthbeat.uuid", "uuid");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));
                    }
                });
            }
        });
    }

    public void synchronizeWithCookieTracking(final Synchronization synchronization) {

        String advertisingId = null;
        try {
            advertisingId = DeviceUtils.getAdvertisingId().get();
        } catch (Exception e) {
            GrowthLink.getInstance().getLogger().warning("Failed to get advertisingId: " + e.getMessage());
        }

        String tmpUrlString = GrowthLink.getInstance().getSyncronizationUrl() + "?applicationId="
            + GrowthLink.getInstance().getApplicationId();
        if (advertisingId != null)
            tmpUrlString += "&advertisingId=" + advertisingId;

        final String urlString = tmpUrlString;


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

    public void synchronizeWithDeviceFingerprint(final Synchronization synchronization) {

        if (synchronization.getClickId() == null)
            return;

        final String uriString = "?clickId=" + synchronization.getClickId();
        GrowthLink.getInstance().handleOpenUrl(Uri.parse(uriString));

    }


}
