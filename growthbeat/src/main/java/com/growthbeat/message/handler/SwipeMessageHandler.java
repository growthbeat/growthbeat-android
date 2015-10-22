package com.growthbeat.message.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.SwipeMessage;
import com.growthbeat.message.view.MessageActivity;

public class SwipeMessageHandler implements MessageHandler {

    private Context context;

    public SwipeMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean handle(final Message message) {

        if (message.getType() != Message.Type.swipe)
            return false;
        if (!(message instanceof SwipeMessage))
            return false;

        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("message", (SwipeMessage) message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;

    }

}
