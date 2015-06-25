package com.growthbeat.message.handler;

import android.content.Context;
import android.content.Intent;

import com.growthbeat.message.model.BannerMessage;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.view.MessageActivity;

public class BannerMessageHandler implements MessageHandler {

	private Context context;

	public BannerMessageHandler(Context context) {
		this.context = context;
	}

	@Override
	public boolean handle(final Message message) {

		if (message.getType() != Message.Type.banner)
			return false;
		if (!(message instanceof BannerMessage))
			return false;

		// new BannerMessageView(context, (BannerMessage) message);
		Intent intent = new Intent(context, MessageActivity.class);
		intent.putExtra("message", (BannerMessage) message);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

		return true;

	}

}
