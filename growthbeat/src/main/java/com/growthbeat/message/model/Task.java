package com.growthbeat.message.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Task extends Model {

    public static enum Orientation {
        vertical, horizontal
    }

    private String id;
    private String applicationId;
    private String name;
    private String description;
    private Orientation orientation;
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

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
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
            if (orientation != null)
                jsonObject.put("orientation", orientation.toString());
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "orientation"))
                setOrientation(Orientation.valueOf(jsonObject.getString("orientation")));
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
