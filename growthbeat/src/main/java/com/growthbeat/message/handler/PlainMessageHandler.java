package com.growthbeat.message.handler;

import android.content.Context;

import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.PlainMessage;

public class PlainMessageHandler extends BaseMessageHandler {

    public PlainMessageHandler(Context context) {
        super(context);
    }

    @Override
    public boolean handle(final Message message, MessageHandler.MessageDonwloadHandler downloadHandler) {

        if (message.getType() != Message.MessageType.plain)
            return false;
        if (!(message instanceof PlainMessage))
            return false;

        ShowMessageHandler.MessageRenderHandler renderHandler = new ShowMessageHandler.MessageRenderHandler() {
            @Override
            public void render() {
                startActivity(message);
            }
        };

        downloadHandler.complete(renderHandler);

        return true;

    }
}
