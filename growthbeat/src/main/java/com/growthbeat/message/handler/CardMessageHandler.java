package com.growthbeat.message.handler;

import android.content.Context;

import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.Message;

public class CardMessageHandler extends BaseMessageHandler {

    public CardMessageHandler(Context context) {
        super(context);
    }

    @Override
    public boolean handle(final Message message, final MessageHandler.MessageDonwloadHandler downloadHandler) {

        if (message.getType() != Message.MessageType.card)
            return false;
        if (!(message instanceof CardMessage))
            return false;

        showMessage(message, downloadHandler);

        return true;

    }

}
