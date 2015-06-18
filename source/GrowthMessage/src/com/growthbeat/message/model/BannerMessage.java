package com.growthbeat.message.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class BannerMessage extends Message {

	private Picture picture;
	private BannerType bannerType;
	private String caption;
	private String text;
	private Position position;
	private int duration;

	public BannerMessage() {
		super();
	}

	public BannerMessage(JSONObject jsonObject) {
		super(jsonObject);
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}
	
	public BannerType getBannerType() {
		return bannerType;
	}

	public void setBannerType(BannerType bannerType) {
		this.bannerType = bannerType;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = super.getJsonObject();

		try {
			if (picture != null)
				jsonObject.put("picture", picture.getJsonObject());
			if (bannerType != null)
				jsonObject.put("bannerType", bannerType.toString());
			if (caption != null)
				jsonObject.put("caption", caption);
			if (text != null)
				jsonObject.put("text", text);
			if (position != null)
				jsonObject.put("position", position.toString());
			jsonObject.put("duration", duration);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "bannerType"))
				setBannerType(BannerType.valueOf(jsonObject.getString("bannerType")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "caption"))
				setCaption(jsonObject.getString("caption"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "text"))
				setText(jsonObject.getString("text"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "position"))
				setPosition(Position.valueOf(jsonObject.getString("position")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "duration"))
				setDuration(jsonObject.getInt("duration"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}
	
	public static enum BannerType {
		onlyImage, imageText
	}
	
	public static enum Position {
		top, bottom
	}

}
