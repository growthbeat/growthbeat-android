package com.growthbeat.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.growthbeat.CatchableThread;
import com.growthbeat.GrowthbeatCore;
import com.growthbeat.GrowthbeatException;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.analytics.EventHandler;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.message.handler.BannerMessageHandler;
import com.growthbeat.message.handler.ImageMessageHandler;
import com.growthbeat.message.handler.MessageHandler;
import com.growthbeat.message.handler.PlainMessageHandler;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.Message;

public class GrowthMessage {

	public static final String LOGGER_DEFAULT_TAG = "GrowthMessage";
	public static final String HTTP_CLIENT_DEFAULT_BASE_URL = "https://api.message.growthbeat.com/";
	private static final int HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
	private static final int HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
	public static final String PREFERENCE_DEFAULT_FILE_NAME = "growthmessage-preferences";

	private static final GrowthMessage instance = new GrowthMessage();
	private final Logger logger = new Logger(LOGGER_DEFAULT_TAG);
	private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(HTTP_CLIENT_DEFAULT_BASE_URL,
			HTTP_CLIENT_DEFAULT_CONNECTION_TIMEOUT, HTTP_CLIENT_DEFAULT_SOCKET_TIMEOUT);
	private final Preference preference = new Preference(PREFERENCE_DEFAULT_FILE_NAME);

	private Context context = null;
	private String applicationId = null;
	private String credentialId = null;

	private boolean initialized = false;
	private List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();

	private GrowthMessage() {
		super();
	}

	public static GrowthMessage getInstance() {
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
		GrowthAnalytics.getInstance().addEventHandler(new EventHandler() {
			@Override
			public void callback(String eventId, Map<String, String> properties) {
				if (eventId != null && eventId.startsWith("Event:" + applicationId + ":GrowthMessage"))
					return;
				recevieMessage(eventId);
			}
		});

		setMessageHandlers(Arrays.asList(new PlainMessageHandler(context), new ImageMessageHandler(context), new BannerMessageHandler(context)));

	}

	private void recevieMessage(final String eventId) {

		final Handler handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {

				logger.info("Receive message...");

				try {

					final Message message = Message.receive(GrowthbeatCore.getInstance().waitClient().getId(), eventId, credentialId);
					if (message == null) {
						logger.warning("Message response is null.");
						return;
					}

					logger.info(String.format("Message is received. (id: %s)", message.getId()));

					handler.post(new Runnable() {
						@Override
						public void run() {
							openMessage(message);
						}
					});

				} catch (GrowthbeatException e) {
					logger.info(String.format("Message is not found.", e.getMessage()));
				}

			}

		}).start();

	}

	private void openMessage(Message message) {

		for (MessageHandler messageHandler : messageHandlers) {
			if (!messageHandler.handle(message))
				continue;

			Map<String, String> properties = new HashMap<String, String>();
			if (message != null && message.getTask() != null)
				properties.put("taskId", message.getTask().getId());
			if (message != null)
				properties.put("messageId", message.getId());

			GrowthAnalytics.getInstance().track("GrowthMessage", "ShowMessage", properties, null);

			break;
		}

	}

	public void selectButton(Button button, Message message) {

		GrowthbeatCore.getInstance().handleIntent(button.getIntent());

		Map<String, String> properties = new HashMap<String, String>();
		if (message != null && message.getTask() != null)
			properties.put("taskId", message.getTask().getId());
		if (message != null)
			properties.put("messageId", message.getId());
		if (button != null && button.getIntent() != null)
			properties.put("intentId", button.getIntent().getId());

		GrowthAnalytics.getInstance().track("GrowthMessage", "SelectButton", properties, null);

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

	public void setMessageHandlers(List<MessageHandler> messageHandlers) {
		this.messageHandlers = messageHandlers;
	}

	public void addMessageHandler(MessageHandler messageHandler) {
		this.messageHandlers.add(messageHandler);
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
			GrowthMessage.getInstance().getLogger().warning(message);
			e.printStackTrace();
		}

	}

}
