package com.growthpush;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.growthbeat.CatchableThread;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.utils.AppUtils;
import com.growthbeat.utils.DeviceUtils;
import com.growthpush.handler.DefaultReceiveHandler;
import com.growthpush.handler.ReceiveHandler;
import com.growthpush.model.Client;
import com.growthpush.model.Environment;
import com.growthpush.model.Event;
import com.growthpush.model.Tag;

public class GrowthPush {

	public static final String LOGGER_DEFAULT_TAG = "GrowthPush";
	public static final String HTTP_CLIENT_DEFAULT_BASE_URL = "https://api.growthpush.com/";
	private static final int HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
	private static final int HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT = 60 * 1000;
	public static final String PREFERENCE_DEFAULT_FILE_NAME = "growthpush-preferences";

	private static final GrowthPush instance = new GrowthPush();
	private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
	private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(HTTP_CLIENT_DEFAULT_BASE_URL,
			HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT, HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT);
	private final Preference preference = new Preference(PREFERENCE_DEFAULT_FILE_NAME);

	private Client client = null;
	private Semaphore semaphore = new Semaphore(1);
	private CountDownLatch latch = new CountDownLatch(1);
	private ReceiveHandler receiveHandler = new DefaultReceiveHandler();

	private String applicationId;
	private String credentialId;
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

		new Thread(new Runnable() {

			@Override
			public void run() {

				com.growthbeat.model.Client growthbeatClient = GrowthbeatCore.getInstance().waitClient();
				client = Client.load();

				if (client != null && client.getGrowthbeatClientId() != null
						&& !client.getGrowthbeatClientId().equals(growthbeatClient.getId()))
					GrowthPush.this.clearClient();

			}

		}).start();

	}

	public void requestRegistrationId(final String senderId, final Environment environment) {

		if (!initialized) {
			logger.warning("Growth Push must be initilaize.");
			return;
		}

		if (client != null)
			return;

		this.environment = environment;

		new Thread(new Runnable() {
			@Override
			public void run() {
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(GrowthbeatCore.getInstance().getContext());
				try {
					String registrationId = gcm.register(senderId);
					registerClient(registrationId);
				} catch (IOException e) {
				}
			}
		}).start();

	}

	public void registerClient(final String registrationId) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {

					semaphore.acquire();

					com.growthbeat.model.Client growthbeatClient = GrowthbeatCore.getInstance().waitClient();
					client = Client.load();
					if (client == null) {
						createClient(growthbeatClient.getId(), registrationId);
						return;
					}

					if ((registrationId != null && !registrationId.equals(client.getToken())) || environment != client.getEnvironment()) {
						updateClient(registrationId);
						return;
					}

					logger.info("Client already registered.");
					latch.countDown();

				} catch (InterruptedException e) {
				} finally {
					semaphore.release();
				}

			}

		}).start();

	}

	private void createClient(final String growthbeatClientId, final String registrationId) {

		try {

			logger.info(String.format("Create client... (growthbeatClientId: %s, token: %s, environment: %s", growthbeatClientId, registrationId, environment));
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

			logger.info(String.format("Updating client... (growthbeatClientId: %s, token: %s, environment: %s)", client.getGrowthbeatClientId(), registrationId,
					environment));
			client.setToken(registrationId);
			client.setEnvironment(environment);
			client = Client.update(client.getGrowthbeatClientId(), credentialId, registrationId, environment);
			logger.info(String.format("Update client success (clientId: %d)", client.getId()));

			Client.save(client);
			latch.countDown();

		} catch (GrowthPushException e) {
			logger.error(String.format("Update client fail. %s", e.getMessage()));
		}

	}

	public void trackEvent(final String name) {
		trackEvent(name, null);
	}

	public void trackEvent(final String name, final String value) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (name == null) {
					logger.warning("Event name cannot be null.");
					return;
				}

				waitClientRegistration();

				logger.info(String.format("Sending event ... (name: %s)", name));
				try {
					Event event = Event.create(GrowthPush.getInstance().client.getGrowthbeatClientId(),
							GrowthPush.getInstance().credentialId, name, value);
					logger.info(String.format("Sending event success. (timestamp: %s)", event.getTimestamp()));
				} catch (GrowthPushException e) {
					logger.error(String.format("Sending event fail. %s", e.getMessage()));
				}

			}

		}).start();

	}

	public void setTag(final String name) {
		setTag(name, null);
	}

	public void setTag(final String name, final String value) {

		new Thread(new Runnable() {

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
					Tag createdTag = Tag.create(GrowthPush.getInstance().client.getGrowthbeatClientId(), credentialId, name, value);
					logger.info(String.format("Sending tag success"));
					Tag.save(createdTag, name);
				} catch (GrowthPushException e) {
					logger.error(String.format("Sending tag fail. %s", e.getMessage()));
				}

			}

		}).start();

	}

	public void setDeviceTags() {
		setTag("Device", DeviceUtils.getModel());
		setTag("OS", "Android " + DeviceUtils.getOsVersion());
		setTag("Language", DeviceUtils.getLanguage());
		setTag("Time Zone", DeviceUtils.getTimeZone());
		setTag("Version", AppUtils.getaAppVersion(GrowthbeatCore.getInstance().getContext()));
		setTag("Build", AppUtils.getAppBuild(GrowthbeatCore.getInstance().getContext()));
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

	private static class Thread extends CatchableThread {

		public Thread(Runnable runnable) {
			super(runnable);
		}

		@Override
		public void uncaughtException(java.lang.Thread thread, Throwable e) {
			String message = "Uncaught Exception: " + e.getClass().getName();
			if (e.getMessage() != null)
				message += "; " + e.getMessage();
			GrowthPush.getInstance().getLogger().warning(message);
			e.printStackTrace();
		}

	}

}
