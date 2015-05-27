package com.growthbeat.analytics;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.growthbeat.CatchableThread;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.GrowthbeatException;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.analytics.model.ClientEvent;
import com.growthbeat.analytics.model.ClientTag;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.utils.AppUtils;
import com.growthbeat.utils.DeviceUtils;

public class GrowthAnalytics {

	public static final String LOGGER_DEFAULT_TAG = "GrowthAnalytics";
	public static final String HTTP_CLIENT_DEFAULT_BASE_URL = "https://api.analytics.growthbeat.com/";
	private static final int HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT = 60 * 1000;
	private static final int HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT = 60 * 1000;
	public static final String PREFERENCE_DEFAULT_FILE_NAME = "growthanalytics-preferences";

	private static final String DEFAULT_NAMESPACE = "Default";
	private static final String CUSTOM_NAMESPACE = "Custom";

	private static final GrowthAnalytics instance = new GrowthAnalytics();
	private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
	private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(HTTP_CLIENT_DEFAULT_BASE_URL,
			HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT, HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT);
	private final Preference preference = new Preference(PREFERENCE_DEFAULT_FILE_NAME);

	private String applicationId = null;
	private String credentialId = null;

	private boolean initialized = false;
	private Date openDate = null;
	private List<EventHandler> eventHandlers = new ArrayList<EventHandler>();

	private GrowthAnalytics() {
		super();
	}

	public static GrowthAnalytics getInstance() {
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

		if (GrowthbeatCore.getInstance().getClient() == null
				|| (GrowthbeatCore.getInstance().getClient().getApplication() != null && !GrowthbeatCore.getInstance().getClient()
						.getApplication().getId().equals(applicationId))) {
			preference.removeAll();
		}

		setBasicTags();

	}

	public void track(String name) {
		track(CUSTOM_NAMESPACE, name, null, null);
	}

	public void track(String name, Map<String, String> properties) {
		track(CUSTOM_NAMESPACE, name, properties, null);
	}

	public void track(String name, TrackOption option) {
		track(CUSTOM_NAMESPACE, name, null, option);
	}

	public void track(String name, Map<String, String> properties, TrackOption option) {
		track(CUSTOM_NAMESPACE, name, properties, option);
	}

	public void track(final String namespace, final String name, final Map<String, String> properties, final TrackOption option) {

		final String eventId = generateEventId(namespace, name);

		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {

				logger.info(String.format("Track event... (eventId: %s)", eventId));

				final Map<String, String> processedProperties = (properties != null) ? properties : new HashMap<String, String>();

				ClientEvent existingClientEvent = ClientEvent.load(eventId);

				if (option == TrackOption.ONCE) {
					if (existingClientEvent != null) {
						logger.info(String.format("Event already sent with once option. (eventId: %s)", eventId));
						return;
					}
				}

				if (option == TrackOption.COUNTER) {
					int counter = 0;
					if (existingClientEvent != null && existingClientEvent.getProperties() != null) {
						try {
							counter = Integer.valueOf(existingClientEvent.getProperties().get("counter"));
						} catch (NumberFormatException e) {
						}
					}
					processedProperties.put("counter", String.valueOf(counter + 1));
				}

				try {
					ClientEvent createdClientEvent = ClientEvent.create(GrowthbeatCore.getInstance().waitClient().getId(), eventId,
							processedProperties, credentialId);
					if (createdClientEvent != null) {
						ClientEvent.save(createdClientEvent);
						logger.info(String.format("Tracking event success. (id: %s, eventId: %s, properties: %s)",
								createdClientEvent.getId(), eventId, processedProperties));
					} else {
						logger.warning("Created client_event is null.");
					}
				} catch (GrowthbeatException e) {
					logger.info(String.format("Tracking event fail. %s", e.getMessage()));
				}

				handler.post(new Runnable() {
					@Override
					public void run() {
						for (EventHandler eventHandler : eventHandlers) {
							eventHandler.callback(eventId, processedProperties);
						}
					}
				});

			}
		}).start();

	}

	public void addEventHandler(EventHandler eventHandler) {
		eventHandlers.add(eventHandler);
	}

	public void tag(String name) {
		tag(CUSTOM_NAMESPACE, name, null);
	}

	public void tag(String name, String value) {
		tag(CUSTOM_NAMESPACE, name, value);
	}

	public void tag(final String namespace, final String name, final String value) {

		final String tagId = generateTagId(namespace, name);
		new Thread(new Runnable() {
			@Override
			public void run() {

				logger.info(String.format("Set tag... (tagId: %s, value: %s)", tagId, value));

				ClientTag existingClientTag = ClientTag.load(tagId);

				if (existingClientTag != null) {
					if (value == existingClientTag.getValue() || (value != null && value.equals(existingClientTag.getValue()))) {
						logger.info(String.format("Tag exists with the same value. (tagId: %s, value: %s)", tagId, value));
						return;
					}
					logger.info(String.format("Tag exists with the other value. (tagId: %s, value: %s)", tagId, value));
				}

				try {
					ClientTag createdClientTag = ClientTag.create(GrowthbeatCore.getInstance().waitClient().getId(), tagId, value,
							credentialId);
					if (createdClientTag != null) {
						ClientTag.save(createdClientTag);
						logger.info(String.format("Setting tag success. (tagId: %s)", tagId));
					} else {
						logger.warning("Created client_tag is null.");
					}
				} catch (GrowthbeatException e) {
					logger.info(String.format("Setting tag fail. %s", e.getMessage()));
				}

			}
		}).start();

	}

	public void open() {
		openDate = new Date();
		track(DEFAULT_NAMESPACE, "Open", null, TrackOption.COUNTER);
		track(DEFAULT_NAMESPACE, "Install", null, TrackOption.ONCE);
	}

	public void close() {
		if (openDate == null)
			return;
		long time = (new Date().getTime() - openDate.getTime()) / 1000;
		openDate = null;
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("time", String.valueOf(time));
		track(DEFAULT_NAMESPACE, "Close", properties, null);
	}

	public void purchase(int price, String category, String product) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("price", String.valueOf(price));
		properties.put("category", category);
		properties.put("product", product);
		track(DEFAULT_NAMESPACE, "Purchase", properties, null);
	}

	public void setUserId(String userId) {
		tag(DEFAULT_NAMESPACE, "UserID", userId);
	}

	public void setName(String name) {
		tag(DEFAULT_NAMESPACE, "Name", name);
	}

	public void setAge(int age) {
		tag(DEFAULT_NAMESPACE, "Age", String.valueOf(age));
	}

	public void setGender(Gender gender) {
		tag(DEFAULT_NAMESPACE, "Gender", gender.getValue());
	}

	public void setLevel(int level) {
		tag(DEFAULT_NAMESPACE, "Level", String.valueOf(level));
	}

	public void setDevelopment(boolean development) {
		tag(DEFAULT_NAMESPACE, "Development", String.valueOf(development));
	}

	public void setDeviceModel() {
		tag(DEFAULT_NAMESPACE, "DeviceModel", DeviceUtils.getModel());
	}

	public void setOS() {
		tag(DEFAULT_NAMESPACE, "OS", "Android " + DeviceUtils.getOsVersion());
	}

	public void setLanguage() {
		tag(DEFAULT_NAMESPACE, "Language", DeviceUtils.getLanguage());
	}

	public void setTimeZone() {
		tag(DEFAULT_NAMESPACE, "TimeZone", DeviceUtils.getTimeZone());
	}

	public void setTimeZoneOffset() {
		tag(DEFAULT_NAMESPACE, "TimeZoneOffset", String.valueOf(DeviceUtils.getTimeZoneOffset()));
	}

	public void setAppVersion() {
		tag(DEFAULT_NAMESPACE, "AppVersion", AppUtils.getaAppVersion(GrowthbeatCore.getInstance().getContext()));
	}

	public void setRandom() {
		tag(DEFAULT_NAMESPACE, "Random", String.valueOf(new Random().nextDouble()));
	}

	public void setAdvertisingId() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(GrowthbeatCore.getInstance().getContext());
					if (adInfo.getId() == null || !adInfo.isLimitAdTrackingEnabled())
						return;
					tag(DEFAULT_NAMESPACE, "AdvertisingID", adInfo.getId());
				} catch (Exception e) {
					logger.warning("Failed to get advertising info: " + e.getMessage());
				}
			}
		}).start();
	}

	public void setTrackingEnabled() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(GrowthbeatCore.getInstance().getContext());
					tag(DEFAULT_NAMESPACE, "TrackingEnabled", String.valueOf(!adInfo.isLimitAdTrackingEnabled()));
				} catch (Exception e) {
					logger.warning("Failed to get advertising info: " + e.getMessage());
				}
			}
		}).start();
	}

	public void setBasicTags() {
		setDeviceModel();
		setOS();
		setLanguage();
		setTimeZone();
		setTimeZoneOffset();
		setAppVersion();
		setAdvertisingId();
		setTrackingEnabled();
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

	private String generateEventId(String namespace, String name) {
		return String.format("Event:%s:%s:%s", applicationId, namespace, name);
	}

	private String generateTagId(final String namespace, String name) {
		return String.format("Tag:%s:%s:%s", applicationId, namespace, name);
	}

	public static enum TrackOption {
		ONCE, COUNTER;
	}

	public static enum Gender {

		MALE("male"), FEMALE("female");
		private String value = null;

		Gender(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

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
			GrowthAnalytics.getInstance().getLogger().warning(message);
			e.printStackTrace();
		}

	}

}
