package com.growthpush.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;

public class Event extends Model {

	private int goalId;

	private long clientId;

	private long timestamp;

	private String value;

	public Event(JSONObject jsonObject) {
		super();
		setJsonObject(jsonObject);
	}

	public static Event create(String clientId, String credentialId, String name, String value) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("clientId", clientId);
		params.put("credentialId", credentialId);
		params.put("name", name);
		if (value != null)
			params.put("value", value);
		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("3/events", params);
		return new Event(jsonObject);
	}

	public int getGoalId() {
		return goalId;
	}

	public void setGoalId(int goalId) {
		this.goalId = goalId;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
			jsonObject.put("goalId", getGoalId());
			jsonObject.put("clientId", getClientId());
			jsonObject.put("timestamp", getTimestamp());
			jsonObject.put("value", getValue());
		} catch (JSONException e) {
		}

		return jsonObject;
	}

	@Override
	public void setJsonObject(JSONObject jsonObject) {
		try {
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "goalId"))
				setGoalId(jsonObject.getInt("goalId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
				setClientId(jsonObject.getLong("clientId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "timestamp"))
				setTimestamp(jsonObject.getLong("timestamp"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "value"))
				setValue(jsonObject.getString("value"));
		} catch (JSONException e) {
		}
	}

}
