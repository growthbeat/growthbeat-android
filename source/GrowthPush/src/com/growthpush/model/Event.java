package com.growthpush.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.growthbeat.model.Model;
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
		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("3/tags", params);
		return new Event(jsonObject);
	}

	@Override
	public JSONObject getJsonObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJsonObject(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

}
