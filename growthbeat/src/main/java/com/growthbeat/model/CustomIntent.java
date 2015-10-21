package com.growthbeat.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class CustomIntent extends Intent {

    private Map<String, String> extra;

    public CustomIntent() {
        super();
        setType(Type.custom);
    }

    public CustomIntent(JSONObject jsonObject) {
        super(jsonObject);
        setType(Type.custom);
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = super.getJsonObject();
        try {
            if (extra != null) {
                JSONObject extraJsonObject = new JSONObject();
                for (Map.Entry<String, String> entry : extra.entrySet())
                    extraJsonObject.put(entry.getKey(), entry.getValue());
                jsonObject.put("extra", extraJsonObject);
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "extra")) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject extraJsonObject = jsonObject.getJSONObject("extra");
                Iterator<String> iterator = extraJsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = extraJsonObject.getString(key);
                    map.put(key, value);
                }
                setExtra(map);
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
