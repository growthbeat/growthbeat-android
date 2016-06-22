package com.growthbeat.message.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Picture extends Model {

	private String id;
	private String applicationId;
	private Extension extension;
	private int width;
	private int height;
	private String name;
	private String url;
	private Date created;
	private Date updated;

	public Picture() {
		super();
	}

	public Picture(JSONObject jsonObject) {
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

	public Extension getExtension() {
		return extension;
	}

	public void setExtension(Extension extension) {
		this.extension = extension;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
			if (extension != null)
				jsonObject.put("extension", extension.toString());
			jsonObject.put("width", width);
			jsonObject.put("height", height);
			if (name != null)
				jsonObject.put("name", name);
			if (url != null)
				jsonObject.put("url", url);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "extension"))
				setExtension(Extension.valueOf(jsonObject.getString("extension")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "width"))
				setWidth(jsonObject.getInt("width"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "height"))
				setHeight(jsonObject.getInt("height"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "name"))
				setName(jsonObject.getString("name"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "url"))
				setUrl(jsonObject.getString("url"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
				setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "updated"))
				setUpdated(DateUtils.parseFromDateTimeString(jsonObject.getString("updated")));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

	public enum Extension {
		png,
		jpg
	}

}
