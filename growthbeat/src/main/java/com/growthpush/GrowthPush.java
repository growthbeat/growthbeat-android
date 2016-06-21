package com.growthpush;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.growthbeat.Growthbeat;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.message.GrowthMessage;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.utils.AppUtils;
import com.growthbeat.utils.DeviceUtils;
import com.growthpush.handler.DefaultReceiveHandler;
import com.growthpush.handler.ReceiveHandler;
import com.growthpush.model.Client;
import com.growthpush.model.Environment;
import com.growthpush.model.Event;
import com.growthpush.model.Tag;

public class GrowthPush {

    private static final GrowthPush instance = new GrowthPush();
    private final Logger logger = new Logger(GrowthPushConstants.LOGGER_DEFAULT_TAG);
    private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(GrowthPushConstants.HTTP_CLIENT_DEFAULT_BASE_URL,
        GrowthPushConstants.HTTP_CLIENT_DEFAULT_CONNECT_TIMEOUT, GrowthPushConstants.HTTP_CLIENT_DEFAULT_READ_TIMEOUT);
    private final Preference preference = new Preference(GrowthPushConstants.PREFERENCE_DEFAULT_FILE_NAME);

    private Client client = null;
    private Semaphore semaphore = new Semaphore(1);
    private CountDownLatch latch = new CountDownLatch(1);
    private ReceiveHandler receiveHandler = new DefaultReceiveHandler();

    private String applicationId;
    private String credentialId;
    private String senderId;
    private Environment environment = null;

    private boolean initialized = false;

    private GrowthPush() {
        super();
    }

    public static GrowthPush getInstance() {
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

        this.applicationId = applicationId;
        this.credentialId = credentialId;

        GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
        this.preference.setContext(GrowthbeatCore.getInstance().getContext());

        setAdvertisingId();
        setTrackingEnabled();
        trackEvent(Event.EventType.Default, "Open", null, null);

    }

    public void requestRegistrationId(final String senderId, final Environment environment) {

        if (!initialized) {
            logger.warning("Growth Push must be initilaize.");
            return;
        }

        this.senderId = senderId;
        this.environment = environment;

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                com.growthbeat.model.Client growthbeatClient = GrowthbeatCore.getInstance().waitClient();
                client = Client.load();

                if (client != null && client.getGrowthbeatClientId() != null
                    && !client.getGrowthbeatClientId().equals(growthbeatClient.getId()))
                    GrowthPush.this.clearClient();

                String token = registerGCM(GrowthbeatCore.getInstance().getContext());
                if (token != null) {
                    logger.info("GCM registration token: " + token);
                    registerClient(token);
                }

            }
        });
    }

    public String registerGCM(final Context context) {
        if (this.senderId == null)
            return null;

        try {
            InstanceID instanceID = InstanceID.getInstance(context);
            return instanceID.getToken(this.senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (Exception e) {
            return null;
        }
    }

    public void registerClient(final String registrationId, Environment environment) {
        this.environment = environment;
        registerClient(registrationId);
    }

    protected void registerClient(final String registrationId) {
        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {

            @Override
            public void run() {

                try {

                    semaphore.acquire();

                    if (client == null) {
                        com.growthbeat.model.Client growthbeatClient = GrowthbeatCore.getInstance().waitClient();
                        createClient(growthbeatClient.getId(), registrationId);
                        return;
                    }

                    if ((registrationId != null && !registrationId.equals(client.getToken())) || environment != client.getEnvironment()) {
                        updateClient(registrationId);
                        return;
                    }

                    logger.info("Client already registered.");

                } catch (InterruptedException e){
                } finally {
                    semaphore.release();
                    latch.countDown();
                }

            }

        });
    }

    private void createClient(final String growthbeatClientId, final String registrationId) {

        try {

            logger.info(String.format("Create client... (growthbeatClientId: %s, token: %s, environment: %s", growthbeatClientId,
                registrationId, environment));
            client = Client.create(growthbeatClientId, applicationId, credentialId, registrationId, environment);
            logger.info(String.format("Create client success (clientId: %d)", client.getId()));
            Client.save(client);
            latch.countDown();

        } catch (GrowthPushException e) {
            logger.error(String.format("Create client fail. %s", e.getMessage()));
        }

    }

    private void updateClient(final String registrationId) {

        try {

            logger.info(String.format("Updating client... (growthbeatClientId: %s, token: %s, environment: %s)",
                client.getGrowthbeatClientId(), registrationId, environment));
            client.setToken(registrationId);
            client.setEnvironment(environment);
            client = Client.update(client.getGrowthbeatClientId(), applicationId, credentialId, registrationId, environment);
            logger.info(String.format("Update client success (clientId: %d)", client.getId()));

            Client.save(client);

        } catch (GrowthPushException e) {
            logger.error(String.format("Update client fail. %s", e.getMessage()));
        } finally {
            latch.countDown();
        }

    }

    public void trackEvent(final String name) {
        trackEvent(name, null);
    }

    public void trackEvent(final String name, final String value) {
        trackEvent(name, null, null);
    }

    public void trackEvent(final String name, final String value, final ShowMessageHandler handler) {
        trackEvent(Event.EventType.Custom, name, value, handler);
    }

    public void trackEvent(final Event.EventType type, final String name, final String value, final ShowMessageHandler handler) {

        if(!initialized) {
            logger.info("call after initialized.");
            return;
        }

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {

            @Override
            public void run() {

                if (name == null) {
                    logger.warning("Event name cannot be null.");
                    return;
                }

                waitClientRegistration();

                logger.info(String.format("Sending event ... (name: %s)", name));
                try {
                    Event event = Event.create(GrowthPush.getInstance().client.getGrowthbeatClientId(), applicationId,
                        GrowthPush.getInstance().credentialId, type, name, value);
                    logger.info(String.format("Sending event success. (timestamp: %s)", event.getTimestamp()));

                    GrowthMessage.getInstance().recevieMessage(event.getGoalId(), client.getGrowthbeatClientId(), handler);

                } catch (GrowthPushException e) {
                    logger.error(String.format("Sending event fail. %s", e.getMessage()));
                }

            }

        });
    }

    public void setTag(final String name) {
        setTag(name, null);
    }

    public void setTag(final String name, final String value) {
        setTag(Tag.TagType.Custom, name, value);
    }

    private void setTag(final Tag.TagType type, final String name, final String value) {

        if(!initialized) {
            logger.info("call after initialized.");
            return;
        }

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {

            @Override
            public void run() {

                if (name == null) {
                    logger.warning("Tag name cannot be null.");
                    return;
                }

                Tag tag = Tag.load(name);
                if (tag != null && (value == null || value.equalsIgnoreCase(tag.getValue()))) {
                    logger.info(String.format("Tag exists with the same value. (name: %s, value: %s)", name, value));
                    return;
                }

                waitClientRegistration();

                logger.info(String.format("Sending tag... (key: %s, value: %s)", name, value));
                try {
                    Tag createdTag = Tag.create(GrowthPush.getInstance().client.getGrowthbeatClientId(), applicationId, credentialId, type, name, value);
                    logger.info(String.format("Sending tag success"));
                    Tag.save(createdTag, name);
                } catch (GrowthPushException e) {
                    logger.error(String.format("Sending tag fail. %s", e.getMessage()));
                }

            }

        });
    }

    public void setDeviceTags() {
        setTag("Device", DeviceUtils.getModel());
        setTag("OS", "Android " + DeviceUtils.getOsVersion());
        setTag("Language", DeviceUtils.getLanguage());
        setTag("Time Zone", DeviceUtils.getTimeZone());
        setTag("Version", AppUtils.getaAppVersion(GrowthbeatCore.getInstance().getContext()));
        setTag("Build", AppUtils.getAppBuild(GrowthbeatCore.getInstance().getContext()));
    }

    private void setAdvertisingId() {
        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String advertisingId = DeviceUtils.getAdvertisingId().get();
                    if (advertisingId != null)
                        setTag(Tag.TagType.Default, "AdvertisingID", advertisingId);
                } catch (Exception e) {
                    logger.warning("Failed to get advertisingId: " + e.getMessage());
                }
            }
        });
    }

    private void setTrackingEnabled() {
        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Boolean trackingEnabled = DeviceUtils.getTrackingEnabled().get();
                    if (trackingEnabled != null)
                        setTag(Tag.TagType.Default, "TrackingEnabled", String.valueOf(trackingEnabled));
                } catch (Exception e) {
                    logger.warning("Failed to get trackingEnabled: " + e.getMessage());
                }

            }
        });
    }

    private void waitClientRegistration() {
        if (client == null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
        }
    }

    public void setReceiveHandler(ReceiveHandler receiveHandler) {
        this.receiveHandler = receiveHandler;
    }

    public ReceiveHandler getReceiveHandler() {
        return receiveHandler;
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

    private void clearClient() {

        this.client = null;
        Client.clear();

    }
}
