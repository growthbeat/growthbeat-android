package com.growthbeat.analytics.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class ClientTag extends Model {

    private String clientId;

    private String tagId;

    private String value;

    private Date created;

    public ClientTag() {
    }

    public ClientTag(JSONObject jsonObject) {
        super();
        setJsonObject(jsonObject);
    }

    public static ClientTag create(String clientId, String tagId, String value, String credentialId) {

        Map<String, Object> params = new HashMap<String, Object>();
        if (clientId != null)
            params.put("clientId", clientId);
        if (tagId != null)
            params.put("tagId", tagId);
        if (value != null)
            params.put("value", value);
        if (credentialId != null)
            params.put("credentialId", credentialId);

        JSONObject jsonObject = GrowthAnalytics.getInstance().getHttpClient().post("1/client_tags", params);
        if (jsonObject == null)
            return null;

        return new ClientTag(jsonObject);

    }

    public static void save(ClientTag clientTag) {
        if (clientTag == null)
            return;
        GrowthAnalytics.getInstance().getPreference().save(clientTag.getTagId(), clientTag.getJsonObject());
    }

    public static ClientTag load(String tagId) {
        JSONObject jsonObject = GrowthAnalytics.getInstance().getPreference().get(tagId);
        if (jsonObject == null)
            return null;
        return new ClientTag(jsonObject);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
            if (clientId != null)
                jsonObject.put("clientId", clientId);
            if (tagId != null)
                jsonObject.put("tagId", tagId);
            if (value != null)
                jsonObject.put("value", value);
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
                setClientId(jsonObject.getString("clientId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "tagId"))
                setTagId(jsonObject.getString("tagId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "value"))
                setValue(jsonObject.getString("value"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.");
        }

    }
}
