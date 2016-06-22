package com.growthbeat.message.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class CardMessage extends Message {

	private Picture picture;
	private int baseWidth;
	private int baseHeight;

	public CardMessage() {
		super();
	}

	public CardMessage(JSONObject jsonObject) {
		super(jsonObject);
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public int getBaseWidth() {
		return baseWidth;
	}

	public void setBaseWidth(int baseWidth) {
		this.baseWidth = baseWidth;
	}

	public int getBaseHeight() {
		return baseHeight;
	}

	public void setBaseHeight(int baseHeight) {
		this.baseHeight = baseHeight;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = super.getJsonObject();

		try {
			if (picture != null)
				jsonObject.put("picture", picture.getJsonObject());
			jsonObject.put("baseWidth", baseWidth);
			jsonObject.put("baseHeight", baseHeight);

		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to get JSON.");
		}

		return jsonObject;

	}

	@Override
	public void setJsonObject(JSONObject jsonObject) {

		if (jsonObject == null)
			return;

		super.setJsonObject(jsonObject);

		try {
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "picture"))
				setPicture(new Picture(jsonObject.getJSONObject("picture")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "baseWidth"))
				setBaseWidth(jsonObject.getInt("baseWidth"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "baseHeight"))
				setBaseHeight(jsonObject.getInt("baseHeight"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

}
