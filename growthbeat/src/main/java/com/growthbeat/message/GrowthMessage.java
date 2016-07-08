package com.growthbeat.message;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.growthbeat.Growthbeat;
import com.growthbeat.GrowthbeatException;
import com.growthbeat.Logger;
import com.growthbeat.message.handler.BaseMessageHandler;
import com.growthbeat.message.handler.CardMessageHandler;
import com.growthbeat.message.handler.MessageHandler;
import com.growthbeat.message.handler.PlainMessageHandler;
import com.growthbeat.message.handler.ShowMessageHandler;
import com.growthbeat.message.handler.SwipeMessageHandler;
import com.growthbeat.message.model.Button;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.NoContentMessage;
import com.growthbeat.message.model.Task;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;
import com.growthpush.model.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GrowthMessage {

    private static final GrowthMessage instance = new GrowthMessage();
    private final Logger logger = new Logger(GrowthMessageConstants.LOGGER_DEFAULT_TAG);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private String applicationId = null;
    private String credentialId = null;
    private boolean initialized = false;
    private List<BaseMessageHandler> messageHandlers = new ArrayList<BaseMessageHandler>();
    private Semaphore messageSemaphore = new Semaphore(1);
    private long lastMessageOpenedTimeMills;
    private boolean showingMessage;
    private MessageImageCacheManager messageImageCacheManager = new MessageImageCacheManager();
    private ConcurrentLinkedQueue<MessageQueue> messageQueue = new ConcurrentLinkedQueue<>();
    private Map<String, ShowMessageHandler> showMessageHandlers = new HashMap<>();

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

        Growthbeat.getInstance().initialize(context, applicationId, credentialId);
        setMessageHandlers(
            Arrays.asList(new PlainMessageHandler(context), new CardMessageHandler(context), new SwipeMessageHandler(context)));

    }

    public void recevieMessage(final int goalId, final String clientId, final ShowMessageHandler handler) {

        Growthbeat.getInstance().getExecutor().execute(new Runnable() {
            @Override
            public void run() {

                logger.info("Receive message...");

                List<Task> tasks = new ArrayList<>();
                try {

                    tasks = Task.getTasks(applicationId, credentialId, goalId);
                    logger.info(String.format("Task exist %d for goalId : %d", tasks.size(), goalId));
                    if (tasks.isEmpty())
                        return;

                } catch (GrowthbeatException e) {
                    if (handler != null)
                        handler.error("Failed to get tasks.");
                    logger.info(String.format("Failed to get tasks. %s", e.getMessage()));
                }

                String uuid = UUID.randomUUID().toString();
                showMessageHandlers.put(uuid, handler);

                for (Task task : tasks) {

                    try {
                        Message message = Message.receive(task.getId(), applicationId, clientId, credentialId);
                        if (message instanceof NoContentMessage) {
                            logger.info("this message is not target client.");
                            continue;
                        }

                        if (message != null)
                            messageQueue.add(new MessageQueue(uuid, message));
                    } catch (GrowthbeatException e) {
                        logger.info(String.format("Failed to get messages. %s", e.getMessage()));
                    }

                }

                openMessageIfExists();

            }

        });
    }

    private void openMessage(final MessageQueue messageJob) {

        MessageHandler.MessageDonwloadHandler downloadHandler = new MessageHandler.MessageDonwloadHandler() {

            @Override
            public void complete(ShowMessageHandler.MessageRenderHandler renderHandler) {

                Growthbeat.getInstance().getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        Client client = Growthbeat.getInstance().waitClient();
                        int incrementCount = Message.receiveCount(client.getId(), applicationId, credentialId,
                            messageJob.getMessage().getTask().getId(), messageJob.getMessage().getId());
                        logger.info(String.format("Success show message (count : %d)", incrementCount));
                    }
                });

                ShowMessageHandler showMessageHandler = showMessageHandlers.get(messageJob.getUuid());

                if (showMessageHandler != null) {
                    showMessageHandler.complete(renderHandler);
                } else {
                    renderHandler.render();
                }
                
            }
        };

        for (MessageHandler messageHandler : messageHandlers) {
            if (!messageHandler.handle(messageJob.getMessage(), downloadHandler))
                continue;

            break;
        }

    }

    public void selectButton(Button button, Message message) {

        Growthbeat.getInstance().handleIntent(button.getIntent());

        JSONObject jsonObject = new JSONObject();

        try {
            if (message != null && message.getTask() != null)
                jsonObject.put("taskId", message.getTask().getId());
            if (message != null)
                jsonObject.put("messageId", message.getId());
            if (button != null && button.getIntent() != null)
                jsonObject.put("intentId", button.getIntent().getId());
        } catch (JSONException e) {
        }

        GrowthPush.getInstance().trackEvent(Event.EventType.message, "SelectButton", jsonObject.toString(), null);

    }

    public void openMessageIfExists() {
        Growthbeat.getInstance().getExecutor().execute(new Runnable() {

            @Override
            public void run() {

                try {
                    messageSemaphore.acquire();

                    long diff = System.currentTimeMillis() - lastMessageOpenedTimeMills;
                    if (showingMessage && diff < GrowthMessageConstants.MIN_TIME_FOR_OVERRIDE_MESSAGE) {
                        return;
                    }

                    final MessageQueue messageJob = messageQueue.poll();
                    if (messageJob == null) {
                        logger.info("Empty message queue.");
                        return;
                    }

                    showingMessage = true;

                    logger.info(String.format("Show Message for %s", messageJob.getMessage().getId()));
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            GrowthMessage.getInstance().openMessage(messageJob);
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

    public void setMessageHandlers(List<BaseMessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    public void addMessageHandler(BaseMessageHandler messageHandler) {
        this.messageHandlers.add(messageHandler);
    }

    public MessageImageCacheManager getMessageImageCacheManager() {
        return messageImageCacheManager;
    }

    public void setMessageImageCacheManager(MessageImageCacheManager messageImageCacheManager) {
        this.messageImageCacheManager = messageImageCacheManager;
    }
}
