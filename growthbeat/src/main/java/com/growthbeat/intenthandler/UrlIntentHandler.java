package com.growthbeat.intenthandler;

import android.content.Context;
import android.net.Uri;

import com.growthbeat.model.Intent;
import com.growthbeat.model.UrlIntent;

public class UrlIntentHandler implements IntentHandler {

	private Context context;

	public UrlIntentHandler(Context context) {
		this.context = context;
	}

	@Override
	public boolean handle(Intent intent) {

		if (intent.getType() != Intent.Type.url)
			return false;

		if (!(intent instanceof UrlIntent))
			return false;

		UrlIntent urlIntent = (UrlIntent) intent;

		if (urlIntent.getUrl() == null)
			return false;
		Uri uri = Uri.parse(urlIntent.getUrl());

		android.content.Intent androidIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
		androidIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(androidIntent);

		return true;

	}

}
