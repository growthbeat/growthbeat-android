package com.growthbeat.message.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.message.model.ImageMessage;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.view.MessageActivity;

public class ImageMessageHandler implements MessageHandler {

    private Context context;

    public ImageMessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean handle(final Message message) {

        if (message.getType() != Message.Type.image)
            return false;
        if (!(message instanceof ImageMessage))
            return false;

        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("message", (ImageMessage) message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;

    }

}
