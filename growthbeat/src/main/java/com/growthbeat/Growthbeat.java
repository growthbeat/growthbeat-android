package com.growthbeat;

import java.util.Arrays;
import java.util.List;

import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.intenthandler.IntentHandler;
import com.growthbeat.intenthandler.NoopIntentHandler;
import com.growthbeat.intenthandler.UrlIntentHandler;
import com.growthbeat.model.Client;
import com.growthbeat.model.GrowthPushClient;
import com.growthbeat.model.Intent;

import android.content.Context;

public class Growthbeat {

	private static final String LOGGER_DEFAULT_TAG = "Growthbeat";
	private static final String HTTP_CLIENT_DEFAULT_BASE_URL = "https://api.growthbeat.com/";
	private static final int HTTP_CLIENT_DEFAULT_CONNECT_TIMEOUT = 60 * 1000;
	private static final int HTTP_CLIENT_DEFAULT_READ_TIMEOUT = 60 * 1000;
	private static final String PREFERENCE_DEFAULT_FILE_NAME = "growthbeat-preferences";

	private static final Growthbeat instance = new Growthbeat();
	private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
	private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(HTTP_CLIENT_DEFAULT_BASE_URL,
			HTTP_CLIENT_DEFAULT_CONNECT_TIMEOUT, HTTP_CLIENT_DEFAULT_READ_TIMEOUT);
	private final GrowthbeatThreadExecutor executor = new GrowthbeatThreadExecutor();
	private final Preference preference = new Preference(PREFERENCE_DEFAULT_FILE_NAME);

	private Context context = null;

	private boolean initialized = false;
	private Client client;

	private List<? extends IntentHandler> intentHandlers;

	private Growthbeat() {
		super();
	}

	public static Growthbeat getInstance() {
		return instance;
	}

	public void initialize(Context context, final String applicationId, final String credentialId) {

		if (initialized)
			return;
		initialized = true;

		if (context == null) {
			logger.warning("The context parameter cannot be null.");
			return;
		}

		this.context = context.getApplicationContext();

		this.intentHandlers = Arrays.asList(new UrlIntentHandler(this.context), new NoopIntentHandler());

		logger.info(String.format("Initializing... (applicationId:%s)", applicationId));

		preference.setContext(Growthbeat.this.context);

		GrowthPushClient growthPushClient = GrowthPushClient.load();
		client = Client.load();

		if (growthPushClient != null) {
			if (client != null && client.getId().equals(growthPushClient.getGrowthbeatClientId())
					&& client.getApplication().getId().equals(growthPushClient.getGrowthbeatApplicationId())
					&& client.getApplication().getId().equals(applicationId)) {
				logger.info(String.format("Client already exists. (id:%s)", client.getId()));
				return;
			}
		} else {
			if (client != null && client.getApplication().getId().equals(applicationId)) {
				logger.info(String.format("Client already exists. (id:%s)", client.getId()));
				return;
			}
		}

		preference.removeAll();
		client = null;

		executor.execute(new Runnable() {

			@Override
			public void run() {

				GrowthPushClient growthPushClient = GrowthPushClient.load();
				if (growthPushClient != null) {
					growthPushClient = GrowthPushClient.findByGrowthPushClientId(growthPushClient.getId(), growthPushClient.getCode());
					logger.info(String.format(
							"Growth Push Client found. Convert GrowthPush Client into Growthbeat Client. (GrowthPushClientId:%d, GrowthbeatClientId:%s)",
							growthPushClient.getId(), growthPushClient.getGrowthbeatClientId()));

					client = Client.findById(growthPushClient.getGrowthbeatClientId(), credentialId);
					if (client == null) {
						logger.info("Failed to convert client.");
						client = null;
						GrowthPushClient.removePreference();
						return;
					}

					Client.save(client);
					logger.info(String.format("Client converted. (id:%s)", client.getId()));

				} else {
					logger.info(String.format("Creating client... (applicationId:%s)", applicationId));
					client = Client.create(applicationId, credentialId);

					if (client == null) {
						logger.info("Failed to create client.");
						return;
					}

					Client.save(client);
					logger.info(String.format("Client created. (id:%s)", client.getId()));
				}
			}

		});

	}

	public void handleIntent(Intent intent) {

		if (intentHandlers == null)
			return;

		for (IntentHandler intentHandler : intentHandlers)
			if (intentHandler.handle(intent))
				break;

	}

	public Client getClient() {
		return client;
	}

	public Client waitClient() {
		while (true) {
			if (client != null)
				return client;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public GrowthbeatHttpClient getHttpClient() {
		return httpClient;
	}

	public GrowthbeatThreadExecutor getExecutor() {
		return executor;
	}

	public Preference getPreference() {
		return preference;
	}

	public Context getContext() {
		return context;
	}

	public void setIntentHandlers(List<? extends IntentHandler> intentHandlers) {
		this.intentHandlers = intentHandlers;
	}

}
