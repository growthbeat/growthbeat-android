package com.growthpush.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;

public class Tag extends Model {

	private int tagId;

	private long clientId;

	private String value;

	public Tag(JSONObject jsonObject) {
		super();
		setJsonObject(jsonObject);
	}

	public static Tag create(String clientId, String credentialId, String name, String value) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (clientId != null)
			params.put("clientId", clientId);
		if (credentialId != null)
			params.put("credentialId", credentialId);
		if (name != null)
			params.put("name", name);
		if (value != null)
			params.put("value", value);

		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("3/tags", params);
		if (jsonObject == null)
			return null;

		return new Tag(jsonObject);

	}

	public static void save(Tag tag, String name) {
		if (tag == null)
			return;
		GrowthPush.getInstance().getPreference().save(name, tag.getJsonObject());
	}

	public static Tag load(String name) {
		JSONObject jsonObject = GrowthPush.getInstance().getPreference().get(name);
		if (jsonObject == null)
			return null;
		return new Tag(jsonObject);
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
