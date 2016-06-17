package com.growthbeat.message.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class SwipeMessage extends Message {

    private SwipeType swipeType;
    private SwipeImages swipeImages;
    private int baseWidth;
    private int baseHeight;

    public SwipeMessage() {
        super();
    }

    public SwipeMessage(JSONObject jsonObject) {
        super(jsonObject);
    }

    public SwipeImages getSwipeImages() {
        return swipeImages;
    }

    public void setSwipeImages(SwipeImages swipeImages) {
        this.swipeImages = swipeImages;
    }

    public SwipeType getSwipeType() {
        return swipeType;
    }

    public void setSwipeType(SwipeType swipeType) {
        this.swipeType = swipeType;
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
            if (swipeImages != null) {
                jsonObject.put("swipeImages", swipeImages.getJsonObject());
            }
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "swipeImages"))
                setSwipeImages(new SwipeImages(jsonObject.getJSONObject("swipeImages")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }
    }

    public static enum SwipeType {
        imageOnly, oneButton
    }
}
