package com.growthpush.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;

public class Tag extends Model {

    private static final String TAG_KEY_FORMAT = "tags:%s";

    private int tagId;

    private long clientId;

    private String value;

    public enum TagType {

        Default("default"), Custom("custom"), Message("message");

        private String typeName;

        TagType(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeName() {
            return typeName;
        }
    };

    public Tag() {
        super();
    }

    public Tag(JSONObject jsonObject) {
        this();
        setJsonObject(jsonObject);
    }

    public static Tag create(String clientId, String applicationId, String credentialId, TagType type, String name, String value) {

        Map<String, Object> params = new HashMap<String, Object>();
        if (clientId != null)
            params.put("clientId", clientId);
        if(applicationId != null);
            params.put("applicationId", applicationId);
        if (credentialId != null)
            params.put("credentialId", credentialId);
        if(type != null)
            params.put("type", type.getTypeName());
        if (name != null)
            params.put("name", name);
        if (value != null)
            params.put("value", value);

        JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("4/tags", params);
        if (jsonObject == null)
            return null;

        return new Tag(jsonObject);

    }

    public static void save(Tag tag, String name) {

        if (tag == null || name == null || name.length() == 0)
            return;

        GrowthPush.getInstance().getPreference().save(String.format(TAG_KEY_FORMAT, name), tag.getJsonObject());

    }

    public static Tag load(String name) {

        if (name == null || name.length() == 0)
            return null;

        return new Tag(GrowthPush.getInstance().getPreference().get(String.format(TAG_KEY_FORMAT, name)));

    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tagId", getTagId());
            jsonObject.put("clientId", getClientId());
            if (value != null)
                jsonObject.put("value", getValue());
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "tagId"))
                setTagId(jsonObject.getInt("tagId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
                setClientId(jsonObject.getLong("clientId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "value"))
                setValue(jsonObject.getString("value"));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.");
        }

    }
}
