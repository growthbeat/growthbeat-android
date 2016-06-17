package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;
import com.growthpush.model.Event;

public class Task extends Model {

    private String id;
    private String applicationId;
    private String name;
    private String description;
    private Date availableFrom;
    private Date availableTo;
    private boolean disabled;
    private Date created;
    private Date updated;

    public Task() {
        super();
    }

    public Task(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static List<Task> getTasks(String applicationId, String credentialId, int goalId) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (applicationId != null)
            params.put("applicationId", applicationId);
        if (credentialId != null)
            params.put("credentialId", credentialId);

        params.put("goalId", goalId);

        JSONArray jsonArray = GrowthPush.getInstance().getHttpClient().postForArray("/1/tasks", params);
        return createList(jsonArray);
    }

    public static List<Task> createList(JSONArray jsonArray) {
        List<Task> tasks = new ArrayList<Task>();
        if (jsonArray == null) {
            return tasks;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject row = null;
            try {
                row = jsonArray.getJSONObject(i);
                tasks.add(new Task(row));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(Date availableFrom) {
        this.availableFrom = availableFrom;
    }

    public Date getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(Date availableTo) {
        this.availableTo = availableTo;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            if (id != null)
                jsonObject.put("id", id);
            if (applicationId != null)
                jsonObject.put("applicationId", applicationId);
            if (name != null)
                jsonObject.put("name", name);
            if (description != null)
                jsonObject.put("description", description);
            if (availableFrom != null)
                jsonObject.put("availableFrom", DateUtils.formatToDateTimeString(availableFrom));
            if (availableTo != null)
                jsonObject.put("availableTo", DateUtils.formatToDateTimeString(availableTo));
            jsonObject.put("disabled", getDisabled());
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
            if (updated != null)
                jsonObject.put("updated", DateUtils.formatToDateTimeString(updated));
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "id"))
                setId(jsonObject.getString("id"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "applicationId"))
                setApplicationId(jsonObject.getString("applicationId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "name"))
                setName(jsonObject.getString("name"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "description"))
                setDescription(jsonObject.getString("description"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "availableFrom"))
                setAvailableFrom(DateUtils.parseFromDateTimeString(jsonObject.getString("availableFrom")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "availableTo"))
                setAvailableTo(DateUtils.parseFromDateTimeString(jsonObject.getString("availableTo")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "disabled"))
                setDisabled(jsonObject.getBoolean("disabled"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "updated"))
                setUpdated(DateUtils.parseFromDateTimeString(jsonObject.getString("updated")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
