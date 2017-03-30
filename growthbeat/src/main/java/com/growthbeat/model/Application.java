package com.growthbeat.model;

import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Application extends Model {

    private String id;
    private String name;
    private Date created;

    public Application() {
        super();
    }

    public Application(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            if (id != null)
                jsonObject.put("id", id);
            if (name != null)
                jsonObject.put("name", name);
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;

    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {

        if (jsonObject == null)
            return;

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "id"))
                setId(jsonObject.getString("id"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "name"))
                setName(jsonObject.getString("name"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.");
        }

    }

}
