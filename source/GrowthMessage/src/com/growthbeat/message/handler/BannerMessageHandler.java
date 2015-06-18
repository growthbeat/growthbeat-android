package com.growthbeat.message.handler;

import android.content.Context;

import com.growthbeat.message.model.BannerMessage;
import com.growthbeat.message.model.Message;
import com.growthbeat.message.view.BannerMessageView;

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

		new BannerMessageView(context, (BannerMessage) message);

		return true;

	}

}
