package com.growthbeat.message.model;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by tabatakatsutoshi on 2016/06/17.
 */
public class Background extends Model {

    private int color;
    float opacity;
    boolean outsideClose;

    public Background(JSONObject jsonObject) {
        super(jsonObject);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public boolean isOutsideClose() {
        return outsideClose;
    }

    public void setOutsideClose(boolean outsideClose) {
        this.outsideClose = outsideClose;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("color", color);
            jsonObject.put("opacity", opacity);
            jsonObject.put("outsideClose", outsideClose);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to get JSON.", e);
        }
        return jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
        if (jsonObject == null)
        return;

        try {

            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "color"))
                setColor(jsonObject.getInt("color"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "opacity"))
                setOpacity(jsonObject.getLong("opacity"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "outsideClose"))
                setOutsideClose(jsonObject.getBoolean("outsideClose"));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }
    }
}