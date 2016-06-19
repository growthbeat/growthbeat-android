package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;

public class SwipeImages extends Model {

    private List<Picture> pictures;

    public SwipeImages() {
        super();
    }

    public SwipeImages(JSONObject jsonObject) {
        super(jsonObject);
    }


    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (pictures != null) {
                JSONArray picturesJsonArray = new JSONArray();
                for (Picture picture : pictures) {
                    picturesJsonArray.put(picture.getJsonObject());
                }
                jsonObject.put("pictures", picturesJsonArray);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get JSON.");
        }

        return jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
        if (jsonObject == null)
            return;

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "pictures")) {
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

}
