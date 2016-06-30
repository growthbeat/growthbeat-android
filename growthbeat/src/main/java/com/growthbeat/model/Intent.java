package com.growthbeat.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Intent extends Model {

	private String id;
	private String applicationId;
	private String name;
	private Type type;
	private Date created;

	protected Intent() {
		super();
	}

	protected Intent(JSONObject jsonObject) {
		super(jsonObject);
	}

	public static Intent getFromJsonObject(JSONObject jsonObject) {

		Intent intent = new Intent(jsonObject);
		switch (intent.getType()) {
		case custom:
			return new CustomIntent(jsonObject);
		case noop:
			return new NoopIntent(jsonObject);
		case url:
			return new UrlIntent(jsonObject);
		default:
			return null;
		}

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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
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
			if (applicationId != null)
				jsonObject.put("applicationId", applicationId);
			if (name != null)
				jsonObject.put("name", name);
			if (type != null)
				jsonObject.put("type", type.toString());
			if (created != null)
				jsonObject.put("created", DateUtils.formatToDateTimeString(created));
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "applicationId"))
				setApplicationId(jsonObject.getString("applicationId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "name"))
				setName(jsonObject.getString("name"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "type"))
				setType(Type.valueOf(jsonObject.getString("type")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
				setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

	public static enum Type {
		custom,
		noop,
		url
	}

}
