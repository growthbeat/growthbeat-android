package com.growthbeat.analytics.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class ClientEvent extends Model {

	private String id;

	private String clientId;

	private String eventId;

	private Map<String, String> properties;

	private Date created;

	public ClientEvent() {
		super();
	}

	private ClientEvent(JSONObject jsonObject) {
		setJsonObject(jsonObject);
	}

	public static ClientEvent create(String clientId, String eventId, Map<String, String> properties, String credentialId) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (clientId != null)
			params.put("clientId", clientId);
		if (eventId != null)
			params.put("eventId", eventId);
		if (properties != null)
			for (Map.Entry<String, String> entry : properties.entrySet())
				params.put(String.format("properties[%s]", entry.getKey()), entry.getValue());
		if (credentialId != null)
			params.put("credentialId", credentialId);

		JSONObject jsonObject = GrowthAnalytics.getInstance().getHttpClient().post("1/client_events", params);
		if (jsonObject == null)
			return null;

		return new ClientEvent(jsonObject);

	}

	public static void save(ClientEvent clientEvent) {
		if (clientEvent == null)
			return;
		GrowthAnalytics.getInstance().getPreference().save(clientEvent.getEventId(), clientEvent.getJsonObject());
	}

	public static ClientEvent load(String eventId) {
		JSONObject jsonObject = GrowthAnalytics.getInstance().getPreference().get(eventId);
		if (jsonObject == null)
			return null;
		return new ClientEvent(jsonObject);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
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
			if (clientId != null)
				jsonObject.put("clientId", clientId);
			if (eventId != null)
				jsonObject.put("eventId", eventId);
			if (properties != null) {
				JSONObject propertiesJsonObject = new JSONObject();
				for (Map.Entry<String, String> entry : properties.entrySet())
					propertiesJsonObject.put(entry.getKey(), entry.getValue());
				jsonObject.put("properties", propertiesJsonObject);
			}
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
				setId(jsonObject.getString("id"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
				setClientId(jsonObject.getString("clientId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "eventId"))
				setEventId(jsonObject.getString("eventId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "properties")) {
				Map<String, String> map = new HashMap<String, String>();
				JSONObject propertiesJsonObject = jsonObject.getJSONObject("properties");
				Iterator<String> iterator = propertiesJsonObject.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					String value = propertiesJsonObject.getString(key);
					map.put(key, value);
				}
				setProperties(map);
			}
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
				setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.");
		}

	}
}
