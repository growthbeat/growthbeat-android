package com.growthbeat.message.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.utils.JSONObjectUtils;

public class SwipeMessage extends Message {

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

        @Override
        public Message createFromParcel(Parcel source) {

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(source.readString());
            } catch (JSONException e) {
                throw new GrowthbeatException("Failed to parse JSON. " + e.getMessage(), e);
            }

            return Message.getFromJsonObject(jsonObject);

        }
    };

	private SwipeType swipeType;
	private List<Picture> pictures;
	private int baseWidth;
	private int baseHeight;

	public SwipeMessage() {
		super();
	}

	public SwipeMessage(JSONObject jsonObject) {
		super(jsonObject);
	}

	public SwipeType getSwipeType() {
		return swipeType;
	}

	public void setSwipeType(SwipeType swipeType) {
		this.swipeType = swipeType;
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(List<Picture> pictures) {
		this.pictures = pictures;
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
			if (swipeType != null)
				jsonObject.put("swipeType", swipeType.toString());
			if (pictures != null) {
                JSONArray jsonArray = new JSONArray();
                for(Picture picture: pictures)
                    jsonArray.put(picture.getJsonObject());
				jsonObject.put("pictures", jsonArray);
			}
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "swipeType"))
				setSwipeType(SwipeType.valueOf(jsonObject.getString("swipeType")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "pictures")) {
				List<Picture> pictures = new ArrayList<>();
				JSONArray jsonArray = jsonObject.getJSONArray("pictures");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject pictureJson = jsonArray.getJSONObject(i);
					pictures.add(new Picture(pictureJson));
				}
				setPictures(pictures);
			}
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "baseWidth"))
				setBaseWidth(jsonObject.getInt("baseWidth"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "baseHeight"))
				setBaseHeight(jsonObject.getInt("baseHeight"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}
	}

	public enum SwipeType {
		imageOnly,
		oneButton
	}

}
