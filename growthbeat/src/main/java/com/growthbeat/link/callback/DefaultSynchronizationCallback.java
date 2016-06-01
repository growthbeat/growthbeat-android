package com.growthbeat.link.callback;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.GrowthbeatCore;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.link.handler.SynchronizationHandler;
import com.growthbeat.link.model.Synchronization;
import com.growthbeat.utils.DeviceUtils;

public class DefaultSynchronizationCallback implements SynchronizationCallback {

    private SynchronizationHandler synchronizationHandler;

    public DefaultSynchronizationCallback(){
        synchronizationHandler = new SynchronizationHandler();
    }


    @Override
    public void onComplete(final Synchronization synchronization) {

        if (synchronization == null)
            return;

        if (synchronization.getInstallReferrer()) {
            synchronizationHandler.synchronizeWithInstallReferrer(synchronization);
            return;
        }

        if (synchronization.getCookieTracking()) {
            synchronizationHandler.synchronizeWithCookieTracking(synchronization);
            return;
        }

        if (synchronization.getDeviceFingerprint()) {
            synchronizationHandler.synchronizeWithDeviceFingerprint(synchronization);
            return;
        }

    }



}
