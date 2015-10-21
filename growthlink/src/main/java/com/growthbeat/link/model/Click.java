package com.growthbeat.link.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.link.GrowthLink;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Click extends Model {

    private String id;
    private Pattern pattern;
    private String clientId;
    private boolean open;
    private boolean install;
    private Date created;
    private Date accessed;

    protected Click() {
        super();
    }

    protected Click(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static Click deeplink(String clientId, String clickId, boolean install, String credentialId) {

        Map<String, Object> params = new HashMap<String, Object>();
        if (clientId != null)
            params.put("clientId", clientId);
        if (clickId != null)
            params.put("clickId", clickId);
        params.put("install", install);
        if (credentialId != null)
            params.put("credentialId", credentialId);

        JSONObject jsonObject = GrowthLink.getInstance().getHttpClient().post("1/deeplink", params);
        if (jsonObject == null)
            return null;

        return new Click(jsonObject);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getAccessed() {
        return accessed;
    }

    public void setAccessed(Date accessed) {
        this.accessed = accessed;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            if (id != null)
                jsonObject.put("id", id);
            if (pattern != null)
                jsonObject.put("pattern", pattern.getJsonObject());
            if (clientId != null)
                jsonObject.put("clientId", clientId);
            jsonObject.put("open", open);
            jsonObject.put("install", install);
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
            if (accessed != null)
                jsonObject.put("accessed", DateUtils.formatToDateTimeString(accessed));
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "id"))
                setId(jsonObject.getString("id"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "pattern"))
                setPattern(new Pattern(jsonObject.getJSONObject("pattern")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
                setClientId(jsonObject.getString("clientId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "open"))
                setOpen(jsonObject.getBoolean("open"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "install"))
                setInstall(jsonObject.getBoolean("install"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "accessed"))
                setAccessed(DateUtils.parseFromDateTimeString(jsonObject.getString("accessed")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
