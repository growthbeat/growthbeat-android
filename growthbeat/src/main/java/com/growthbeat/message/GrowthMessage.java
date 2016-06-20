package com.growthbeat.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.GrowthbeatCore;
import com.growthbeat.GrowthbeatException;
import com.growthbeat.Logger;
import com.growthbeat.Preference;
import com.growthbeat.analytics.EventHandler;
import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.http.GrowthbeatHttpClient;
import com.growthbeat.message.handler.ImageMessageHandler;
import com.growthbeat.message.handler.MessageHandler;
import com.growthbeat.message.handler.PlainMessageHandler;
import com.growthbeat.message.handler.SwipeMessageHandler;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.Task;
import com.growthpush.GrowthPush;

public class GrowthMessage {

    private static final GrowthMessage instance = new GrowthMessage();
    private final Logger logger = new Logger(GrowthMessageConstants.LOGGER_DEFAULT_TAG);
    private final GrowthbeatHttpClient httpClient = new GrowthbeatHttpClient(GrowthMessageConstants.HTTP_CLIENT_DEFAULT_BASE_URL,
        GrowthMessageConstants.HTTP_CLIENT_DEFAULT_CONNECT_TIMEOUT, GrowthMessageConstants.HTTP_CLIENT_DEFAULT_READ_TIMEOUT);
    private final Preference preference = new Preference(GrowthMessageConstants.PREFERENCE_DEFAULT_FILE_NAME);

    private String applicationId = null;
    private String credentialId = null;

    private boolean initialized = false;
    private List<MessageHandler> messageHandlers = new ArrayList<MessageHandler>();

    private Semaphore messageSemaphore = new Semaphore(1);
    private long lastMessageOpenedTimeMills;
    private boolean showingMessage;
    private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

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

        this.applicationId = applicationId;
        this.credentialId = credentialId;
        this.showingMessage = false;
        this.lastMessageOpenedTimeMills = System.currentTimeMillis();

        GrowthbeatCore.getInstance().initialize(context, applicationId, credentialId);
        this.preference.setContext(GrowthbeatCore.getInstance().getContext());
        if (GrowthbeatCore.getInstance().getClient() == null
            || (GrowthbeatCore.getInstance().getClient().getApplication() != null && !GrowthbeatCore.getInstance().getClient()
            .getApplication().getId().equals(applicationId))) {
            preference.removeAll();
        }

//        GrowthAnalytics.getInstance().initialize(context, applicationId, credentialId);
//        GrowthAnalytics.getInstance().addEventHandler(new EventHandler() {
//            @Override
//            public void callback(String eventId, Map<String, String> properties) {
//                if (eventId != null && eventId.startsWith("Event:" + applicationId + ":GrowthMessage"))
//                    return;
//                recevieMessage(eventId);
//            }
//        });

        setMessageHandlers(Arrays.asList(new PlainMessageHandler(context), new ImageMessageHandler(context), new SwipeMessageHandler(context)));

    }

    public void recevieMessage(final int goalId, final String clientId) {

        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                logger.info("Receive message...");

                try {

                    List<Task> tasks = Task.getTasks(applicationId, credentialId, goalId);
                    logger.info(String.format("Task exist %d for goalId : %d", tasks.size(), goalId));
                    for (Task task : tasks) {
                        Message message = Message.receive(task.getId(), clientId, credentialId);
                        if(message != null)
                            messageQueue.add(message);
                    }

                    openMessageIfExists();

                } catch (GrowthbeatException e) {
                    logger.info(String.format("Failed to get message. %s", e.getMessage()));
                }

            }

        });
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

    public void openMessageIfExists() {
        GrowthbeatCore.getInstance().getExecutor().execute(new Runnable() {

            @Override
            public void run() {

                try {
                    messageSemaphore.acquire();

                    long diff = System.currentTimeMillis() - lastMessageOpenedTimeMills;
                    if (showingMessage &&  diff < GrowthMessageConstants.MIN_TIME_FOR_OVERRIDE_MESSAGE) {
                        return;
                    }
                    final Message message = messageQueue.poll();
                    showingMessage = true;

                    logger.info(String.format("Show Message for %s", message.getId()));
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            GrowthMessage.getInstance().openMessage(message);
                        }
                    });
                    lastMessageOpenedTimeMills = System.currentTimeMillis();

                } catch (InterruptedException e) {
                } finally {
                    messageSemaphore.release();
                }

            }
        });
    }

    public void notifyPopNextMessage() {
        scheduledThreadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                showingMessage = false;
                openMessageIfExists();
            }
        }, GrowthMessageConstants.POP_NEX_MESSAGE_DELAY, TimeUnit.MILLISECONDS);
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
}
