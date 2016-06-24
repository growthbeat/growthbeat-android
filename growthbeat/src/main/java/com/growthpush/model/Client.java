package com.growthpush.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthpush.GrowthPush;

/**
 * Created by Shigeru Ogawa on 13/08/12.
 */
public class Client extends Model {

	private String id;
	private String applicationId;
	private String token;
	private Environment environment;
	private Status status;
	private Date created;

	public Client() {
		super();
	}

	public Client(JSONObject jsonObject) {
		super();
		setJsonObject(jsonObject);
	}

	public static Client load() {

		JSONObject clientJsonObject = GrowthPush.getInstance().getPreference().get(Client.class.getName());
		if (clientJsonObject == null)
			return null;

		Client client = new Client();
		client.setJsonObject(clientJsonObject);

		return client;

	}

	public static synchronized void save(Client client) {

		if (client == null)
			throw new IllegalArgumentException("Argument client cannot be null.");

		GrowthPush.getInstance().getPreference().save(Client.class.getName(), client.getJsonObject());

	}

	public static void clear() {
		GrowthPush.getInstance().getPreference().remove(Client.class.getName());
	}

	public static Client create(String clientId, String applicationId, String credentialId, String token, Environment environment) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (clientId != null)
			params.put("clientId", clientId);
		if (applicationId != null)
			params.put("applicationId", applicationId);
		if (credentialId != null)
			params.put("credentialId", credentialId);
		if (token != null)
			params.put("token", token);
		if (environment != null)
			params.put("environment", environment.toString());
		params.put("os", "android");
		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("4/clients", params);

		return new Client(jsonObject);

	}

	public static Client update(String clientId, String applicationId, String credentialId, String token, Environment environment) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (applicationId != null)
			params.put("applicationId", applicationId);
		if (credentialId != null)
			params.put("credentialId", credentialId);
		if (token != null)
			params.put("token", token);
		if (environment != null)
			params.put("environment", environment.toString());

		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().put("4/clients/" + clientId, params);

		return new Client(jsonObject);
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
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
			jsonObject.put("id", getId());
			jsonObject.put("applicationId", getApplicationId());
			jsonObject.put("token", getToken());
			if (getEnvironment() != null)
				jsonObject.put("environment", getEnvironment().toString());
			if (getStatus() != null)
				jsonObject.put("status", getStatus().toString());
			if (getCreated() != null)
				jsonObject.put("created", DateUtils.formatToDateTimeString(getCreated()));
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
			if (jsonObject.has("id"))
				setId(jsonObject.getString("id"));
			if (jsonObject.has("applicationId"))
				setApplicationId(jsonObject.getString("applicationId"));
			if (jsonObject.has("token"))
				setToken(jsonObject.getString("token"));
			if (jsonObject.has("environment"))
				setEnvironment(Environment.valueOf(jsonObject.getString("environment")));
			if (jsonObject.has("status"))
				setStatus(Status.valueOf(jsonObject.getString("status")));
			if (jsonObject.has("created"))
				setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.");
		}

	}

	public enum Status {
		unknown,
		validating,
		active,
		inactive,
		invalid
	}

}
