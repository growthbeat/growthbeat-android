package com.growthbeat.link.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;

public class Link extends Model {

	private String id;
	private String alias;
	private String applicationId;
	private String name;

	protected Link() {
		super();
	}

	protected Link(JSONObject jsonObject) {
		super(jsonObject);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = new JSONObject();

		try {
			if (id != null)
				jsonObject.put("id", id);
			if (alias != null)
				jsonObject.put("alias", alias);
			if (applicationId != null)
				jsonObject.put("applicationId", applicationId);
			if (name != null)
				jsonObject.put("name", name);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "alias"))
				setAlias(jsonObject.getString("alias"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "applicationId"))
				setApplicationId(jsonObject.getString("applicationId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "name"))
				setName(jsonObject.getString("name"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

}
