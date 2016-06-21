package com.growthbeat.link.callback;

import com.growthbeat.link.handler.SynchronizationHandler;
import com.growthbeat.link.model.Synchronization;

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
