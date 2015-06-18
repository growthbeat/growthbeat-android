package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class SwipeMessage extends Message {

	private SwipeType swipeType;

	private List<Picture> pictures;

	public SwipeMessage() {
		super();
	}

	public SwipeMessage(JSONObject jsonObject) {
		super(jsonObject);
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
	}

	public SwipeType getSwipeType() {
		return swipeType;
	}

	public void setSwipeType(SwipeType swipeType) {
		this.swipeType = swipeType;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = super.getJsonObject();

		try {
			if (swipeType != null)
				jsonObject.put("swipeType", swipeType.toString());
			if (pictures != null)
				jsonObject.put("pictures", pictures);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "swipeType"))
				setSwipeType(SwipeType.valueOf(jsonObject.getString("swipeType")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "pictures")){
				JSONArray picturesJsonArray = jsonObject.getJSONArray("pictures");
				List<Picture> pictures = new ArrayList<Picture>(picturesJsonArray.length());
				for (int i = 0; i < picturesJsonArray.length(); i++)
					pictures.add(new Picture(picturesJsonArray.getJSONObject(i)));
				setPictures(pictures);
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}
	}

	public static enum SwipeType {
		imageOnly, oneButton, buttons
	}
}
