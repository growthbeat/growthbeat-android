package com.growthbeat.message.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class ImageButton extends Button {

	private Picture picture;

	public ImageButton() {
		super();
	}

	public ImageButton(JSONObject jsonObject) {
		super(jsonObject);
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = super.getJsonObject();

		try {
			if (picture != null)
				jsonObject.put("picture", picture.getJsonObject());
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
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

}
