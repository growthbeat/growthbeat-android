package com.growthbeat.message.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.message.model.CardMessage;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.view.MessageActivity;

public class CardMessageHandler implements MessageHandler {

    private Context context;

    public CardMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean handle(final Message message) {

        if (message.getType() != Message.MessageType.image)
            return false;
        if (!(message instanceof CardMessage))
            return false;

        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("message", (CardMessage) message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;

    }

}
