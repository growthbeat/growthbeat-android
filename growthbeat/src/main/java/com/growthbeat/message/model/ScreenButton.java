package com.growthbeat.message.model;

import org.json.JSONObject;

public class ScreenButton extends Button {

	public ScreenButton() {
		super();
	}

	public ScreenButton(JSONObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = super.getJsonObject();
		return jsonObject;

	}

	@Override
	public void setJsonObject(JSONObject jsonObject) {

		if (jsonObject == null)
			return;

		super.setJsonObject(jsonObject);

	}

}
