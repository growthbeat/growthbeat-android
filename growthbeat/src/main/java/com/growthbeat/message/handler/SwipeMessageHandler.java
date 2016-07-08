package com.growthbeat.message.handler;

import android.content.Context;

import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.SwipeMessage;

public class SwipeMessageHandler extends BaseMessageHandler {

    public SwipeMessageHandler(Context context) {
        super(context);
    }

    @Override
    public boolean handle(final Message message, MessageHandler.MessageDonwloadHandler downloadHandler) {

        if (message.getType() != Message.MessageType.swipe)
            return false;
        if (!(message instanceof SwipeMessage))
            return false;

        showMessage(message, downloadHandler);

        return true;

    }

}
