package com.growthbeat.message.handler;

import com.growthbeat.message.model.Message;
import com.growthbeat.message.model.PlainMessage;
import com.growthbeat.message.view.MessageActivity;

import android.content.Context;
import android.content.Intent;

public class PlainMessageHandler implements MessageHandler {

	private Context context;

	public PlainMessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public boolean handle(final Message message) {

		if (message.getType() != Message.MessageType.plain)
			return false;
		if (!(message instanceof PlainMessage))
			return false;

		Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra("message", (PlainMessage) message);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		return true;

	}
}
