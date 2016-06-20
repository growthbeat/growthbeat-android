package com.growthbeat.message.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class SwipeMessage extends Message {

    public enum SwipeType {
        imageOnly, oneButton
    }
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
                jsonObject.put("pictures", pictures);
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "swipeImages")) {
                List<Picture> pictures = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(jsonObject.getString("pictures"));
                for(int i = 0; i < jsonArray.length(); i++) {
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

}
