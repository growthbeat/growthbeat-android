package com.growthbeat.model;

import org.json.JSONObject;

public class NoopIntent extends Intent {

    public NoopIntent() {
        super();
        setType(Type.noop);
    }

    public NoopIntent(JSONObject jsonObject) {
        super(jsonObject);
        setType(Type.noop);
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = super.getJsonObject();
        return jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
        super.setJsonObject(jsonObject);
    }

}
