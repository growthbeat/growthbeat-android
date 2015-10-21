package com.growthbeat.link.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Intent;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Pattern extends Model {

    private String id;
    private String url;
    private Link link;
    private Intent intent;
    private Date created;
    private Date updated;

    protected Pattern() {
        super();
    }

    protected Pattern(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
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
            if (url != null)
                jsonObject.put("url", url);
            if (link != null)
                jsonObject.put("link", link.getJsonObject());
            if (intent != null)
                jsonObject.put("intent", intent.getJsonObject());
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
            if (updated != null)
                jsonObject.put("updated", DateUtils.formatToDateTimeString(updated));
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "url"))
                setUrl(jsonObject.getString("url"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "link"))
                setLink(new Link(jsonObject.getJSONObject("link")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "intent"))
                setIntent(Intent.getFromJsonObject(jsonObject.getJSONObject("intent")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "updated"))
                setUpdated(DateUtils.parseFromDateTimeString(jsonObject.getString("updated")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
