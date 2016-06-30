package com.growthbeat.message.handler;

import com.growthbeat.message.MessageQueue;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.SwipeMessage;
import com.growthbeat.message.view.MessageActivity;

import android.content.Context;
import android.content.Intent;

public class SwipeMessageHandler implements MessageHandler {

	private Context context;

	public SwipeMessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public boolean handle(final MessageQueue messageJob) {

        Message message = messageJob.getMessage();
		if (message.getType() != Message.MessageType.swipe)
			return false;
		if (!(message instanceof SwipeMessage))
			return false;

		Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra("message", (SwipeMessage) message);
        intent.putExtra("uuid", messageJob.getUuid());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		return true;

	}

}
