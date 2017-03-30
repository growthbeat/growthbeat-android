package com.growthbeat.model;

import org.json.JSONObject;

public abstract class Model {

    public Model() {
        super();
    }

    public Model(JSONObject jsonObject) {
        this();
        setJsonObject(jsonObject);
    }

    public abstract JSONObject getJsonObject();

    public abstract void setJsonObject(JSONObject jsonObject);

}
