package com.growthbeat.model;

import com.growthbeat.Growthbeat;
import com.growthbeat.Preference;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;
import com.growthpush.model.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GrowthPushClient extends Model {

    private static final String PREFERENCE_DEFAULT_FILE_NAME = "growthpush-preferences";
    private static final String PREFERENCE_CLIENT_KEY = "client";
    private static final String PREFERENCE_TAG_KEY = "tags";
    private static final Preference preference = new Preference(Growthbeat.getInstance().getContext(), PREFERENCE_DEFAULT_FILE_NAME);

    private long id;
    private int applicationId;
    private String code;
    private String growthbeatClientId;
    private String growthbeatApplicationId;
    private String token;
    private String environment;
    private String status;
    private Date created;

    public GrowthPushClient(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static GrowthPushClient load() {

        JSONObject jsonObject = preference.get(PREFERENCE_CLIENT_KEY);
        if (jsonObject == null)
            return null;

        return new GrowthPushClient(jsonObject);

    }

    public static void removePreference() {

        JSONObject loadedTags = preference.get(PREFERENCE_TAG_KEY);
        if (loadedTags != null) {
            Iterator<String> keys = loadedTags.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    Tag tag = new Tag(loadedTags.getJSONObject(key));
                    if (tag != null)
                        Tag.save(tag, Tag.TagType.custom, key);
                } catch (JSONException e) {
                    GrowthPush.getInstance().getLogger().warning(String.format("Parsed error. tag (name: %s)", key));
                }
            }
        }

        preference.removeAll();
    }

    public static GrowthPushClient findByGrowthPushClientId(long growthpushClientId, String code) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code);

        JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().get("1/clients/" + growthpushClientId, params);
        if (jsonObject == null)
            return null;

        return new GrowthPushClient(jsonObject);

    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("applicationId", applicationId);
            if (code != null)
                jsonObject.put("code", code);
            if (growthbeatClientId != null)
                jsonObject.put("growthbeatClientId", growthbeatClientId);
            if (growthbeatApplicationId != null)
                jsonObject.put("growthbeatApplicationId", growthbeatApplicationId);
            if (token != null)
                jsonObject.put("token", token);
            if (environment != null)
                jsonObject.put("environment", environment);
            if (status != null)
                jsonObject.put("status", status);
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
                setId(jsonObject.getLong("id"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "applicationId"))
                setApplicationId(jsonObject.getInt("applicationId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "code"))
                setCode(jsonObject.getString("code"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "growthbeatClientId"))
                setGrowthbeatClientId(jsonObject.getString("growthbeatClientId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "growthbeatApplicationId"))
                setGrowthbeatApplicationId(jsonObject.getString("growthbeatApplicationId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "token"))
                setToken(jsonObject.getString("token"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "environment"))
                setEnvironment(jsonObject.getString("environment"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "status"))
                setStatus(jsonObject.getString("status"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.");
        }

    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGrowthbeatClientId() {
        return this.growthbeatClientId;
    }

    public void setGrowthbeatClientId(String growthbeatClientId) {
        this.growthbeatClientId = growthbeatClientId;
    }

    public String getGrowthbeatApplicationId() {
        return this.growthbeatApplicationId;
    }

    public void setGrowthbeatApplicationId(String growthbeatApplicationId) {
        this.growthbeatApplicationId = growthbeatApplicationId;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
