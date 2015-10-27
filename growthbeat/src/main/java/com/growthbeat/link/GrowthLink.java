package com.growthbeat.link;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.GrowthbeatCore;
import com.growthbeat.GrowthbeatException;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.link.FingerprintReceiver.Callback;
import com.growthbeat.link.callback.DefaultSynchronizationCallback;
import com.growthbeat.link.callback.SynchronizationCallback;
import com.growthbeat.link.handler.DefaultInstallReferrerReceiveHandler;
import com.growthbeat.link.handler.InstallReferrerReceiveHandler;
import com.growthbeat.link.model.Click;
import com.growthbeat.link.model.Synchronization;
import com.growthbeat.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GrowthLink {

    private static final String LOGGER_DEFAULT_TAG = "GrowthLink";
    private static final String HTTP_CLIENT_DEFAULT_BASE_URL = "https://api.link.growthbeat.com/";
    private static final String DEFAULT_SYNCRONIZATION_URL = "https://gbt.io/l/synchronize";
    private static final String DEFAULT_FINGERPRINT_URL = "https://gbt.io/l/fingerprints";
    private static final int HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
    private static final int HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT = 60 * 1000;
    private static final String PREFERENCE_DEFAULT_FILE_NAME = "growthlink-preferences";

    private static final String INSTALL_REFERRER_KEY = "installReferrer";

    private static final GrowthLink instance = new GrowthLink();
    private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
    private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(HTTP_CLIENT_DEFAULT_BASE_URL,
        HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT, HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT);
    private final Preference preference = new Preference(PREFERENCE_DEFAULT_FILE_NAME);

    private Context context = null;
    private String applicationId = null;
    private String credentialId = null;

    private String syncronizationUrl = DEFAULT_SYNCRONIZATION_URL;
    private String fingerprintUrl = DEFAULT_FINGERPRINT_URL;

    private boolean initialized = false;
    private boolean firstSession = false;
    private CountDownLatch installReferrerLatch = new CountDownLatch(1);

    private SynchronizationCallback synchronizationCallback = new DefaultSynchronizationCallback();
    private InstallReferrerReceiveHandler installReferrerReceiveHandler = new DefaultInstallReferrerReceiveHandler();

    private GrowthLink() {
        super();
    }

    public static GrowthLink getInstance() {
        return instance;
    }

    public void initialize(final Context context, final String applicationId, final String credentialId) {
        if (initialized)
            return;
        initialized = true;

        if (context == null) {
            logger.warning("The context parameter cannot be null.");
            return;
        }

        this.context = context.getApplicationContext();
        this.applicationId = applicationId;
        this.credentialId = credentialId;

        GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
        this.preference.setContext(GrowthbeatCore.getInstance().getContext());
        if (GrowthbeatCore.getInstance().getClient() == null
            || (GrowthbeatCore.getInstance().getClient().getApplication() != null && !GrowthbeatCore.getInstance().getClient()
            .getApplication().getId().equals(applicationId))) {
            preference.removeAll();
        }

        GrowthAnalytics.getInstance().initialize(context, applicationId, credentialId);
        synchronize();
    }

    public void handleOpenUrl(Uri uri) {

        if (uri == null)
            return;

        final String clickId = uri.getQueryParameter("clickId");
        if (clickId == null) {
            logger.info("Unabled to get clickId from url.");
            return;
        }

        final String uuid = uri.getQueryParameter("uuid");
        if (uuid != null) {
            GrowthAnalytics.getInstance().setUUID(uuid);
        }

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                logger.info("Deeplinking...");

                try {

                    final Click click = Click.deeplink(GrowthbeatCore.getInstance().waitClient().getId(), clickId, firstSession,
                        credentialId);
                    if (click == null || click.getPattern() == null || click.getPattern().getLink() == null) {
                        logger.error("Failed to deeplink.");
                        return;
                    }

                    logger.info(String.format("Deeplink success. (clickId: %s)", click.getId()));

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            Map<String, String> properties = new HashMap<String, String>();
                            properties.put("linkId", click.getPattern().getLink().getId());
                            properties.put("patternId", click.getPattern().getId());
                            if (click.getPattern().getIntent() != null)
                                properties.put("intentId", click.getPattern().getIntent().getId());

                            if (firstSession) {
                                GrowthAnalytics.getInstance().track("GrowthLink", "Install", properties, null);
                                if (click.getPattern().getLink() != null && click.getPattern().getLink().getId() != null) {
                                    GrowthAnalytics.getInstance().tag("GrowthLink", "InstallLink", click.getPattern().getLink().getId());
                                }
                            }


                            GrowthAnalytics.getInstance().track("GrowthLink", "Open", properties, null);

                            firstSession = false;

                            if (click.getPattern().getIntent() != null) {
                                GrowthbeatCore.getInstance().handleIntent(click.getPattern().getIntent());
                            }

                        }
                    });

                } catch (GrowthbeatException e) {
                    logger.info(String.format("Deeplink is not found.", e.getMessage()));
                }

            }

        });
    }

    private void synchronize() {

        logger.info("Check initialization...");
        if (Synchronization.load() != null) {
            logger.info("Already initialized.");
            return;
        }
        firstSession = true;

        FingerprintReceiver.getFingerprintParameters(context, fingerprintUrl, new Callback() {
            @Override
            public void onComplete(final String fingerprintParameters) {
                GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("Synchronizing...");
                        try {
                            String version = AppUtils.getaAppVersion(context);
                            final Synchronization synchronization = Synchronization.synchronize(applicationId, version,
                                fingerprintParameters, credentialId);
                            if (synchronization == null) {
                                logger.error("Failed to Synchronize.");
                                return;
                            }

                            logger.info(String.format(
                                "Synchronize success. (installReferrer: %s, cookieTracking: %s, deviceFingerprint: %s, clickId: %s)",
                                synchronization.getInstallReferrer(), synchronization.getCookieTracking(),
                                synchronization.getDeviceFingerprint(), synchronization.getClickId()));

                            if (GrowthLink.this.synchronizationCallback != null) {
                                GrowthLink.this.synchronizationCallback.onComplete(synchronization);
                            }

                        } catch (GrowthbeatException e) {
                            logger.info(String.format("Synchronization is not found. %s", e.getMessage()));
                        }

                    }

                });
            }
        });

    }

    public Context getContext() {
        return context;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public Logger getLogger() {
        return logger;
    }

    public GrowthbeatHttpClient getHttpClient() {
        return httpClient;
    }

    public Preference getPreference() {
        return preference;
    }

    public String getSyncronizationUrl() {
        return syncronizationUrl;
    }

    public void setSyncronizationUrl(String syncronizationUrl) {
        this.syncronizationUrl = syncronizationUrl;
    }

    public String getFingerprintUrl() {
        return fingerprintUrl;
    }

    public void setFingerprintUrl(String fingerprintUrl) {
        this.fingerprintUrl = fingerprintUrl;
    }

    public String getInstallReferrer() {
        return this.preference.getString(INSTALL_REFERRER_KEY);
    }

    public String waitInstallReferrer(long timeout) {
        try {
            installReferrerLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
        return getInstallReferrer();
    }

    public void setInstallReferrer(String installReferrer) {
        this.preference.save(INSTALL_REFERRER_KEY, installReferrer);
        this.installReferrerLatch.countDown();
    }

    public SynchronizationCallback getSynchronizationCallback() {
        return synchronizationCallback;
    }

    public void setSynchronizationCallback(SynchronizationCallback synchronizationCallback) {
        this.synchronizationCallback = synchronizationCallback;
    }

    public InstallReferrerReceiveHandler getInstallReferrerReceiveHandler() {
        return installReferrerReceiveHandler;
    }

    public void setInstallReferrerReceiveHandler(InstallReferrerReceiveHandler installReferrerReceiveHandler) {
        this.installReferrerReceiveHandler = installReferrerReceiveHandler;
    }
}
