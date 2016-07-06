package com.growthbeat.message.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.message.model.Message;
import com.growthbeat.message.view.MessageActivity;

public abstract class BaseMessageHandler implements MessageHandler {

    protected Context context;
    private ShowMessageHandler.MessageRenderHandler renderHandler;

    public BaseMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public abstract boolean handle(final Message message, MessageHandler.MessageDonwloadHandler downloadHandler);

    protected void showMessage(final Message message, final MessageDonwloadHandler downloadHandler) {

        this.renderHandler = new ShowMessageHandler.MessageRenderHandler() {
            @Override
            public void render() {
                startActivity(message);
            }
        };

        ImageDownloader imageDownloader = new ImageDownloader(context, message, context.getResources().getDisplayMetrics().density, new ImageDownloader.Callback() {

            @Override
            public void success() {
                downloadHandler.complete(renderHandler);
            }

            @Override
            public void failure() {
            }

        });

        imageDownloader.download();

    }

    protected void startActivity(Message message) {

        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("message", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

}
