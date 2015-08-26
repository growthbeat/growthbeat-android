package com.growthbeat.link.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.analytics.GrowthAnalytics;
import com.growthbeat.link.GrowthLink;
import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;

public class Synchronization extends Model {

	private static final String PREFERENCE_SYNCHRONIZATION_KEY = "synchronization";

	private boolean browser;
	private boolean installReferrer;
	private String clickToken;

	protected Synchronization() {
		super();
	}

	protected Synchronization(JSONObject jsonObject) {
		super(jsonObject);
	}

	public static Synchronization synchronize(String applicationId, String version, String credentialId, String userAgent, String fingerprintParameters) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (applicationId != null)
			params.put("applicationId", applicationId);
		params.put("os", "android");
		if (version != null)
			params.put("version", version);
		if (credentialId != null)
			params.put("credentialId", credentialId);
		if (userAgent != null)
			params.put("userAgent", userAgent);
		if (fingerprintParameters != null)
			params.put("fingerprintParameters", fingerprintParameters);
		JSONObject jsonObject = GrowthLink.getInstance().getHttpClient().post("/1/synchronize", params);
		if (jsonObject == null)
			return null;

		return new Synchronization(jsonObject);

	}

	public static void save(Synchronization synchronization) {
		if (synchronization == null)
			return;
		GrowthAnalytics.getInstance().getPreference().save(PREFERENCE_SYNCHRONIZATION_KEY, synchronization.getJsonObject());
	}

	public static Synchronization load() {
		JSONObject jsonObject = GrowthAnalytics.getInstance().getPreference().get(PREFERENCE_SYNCHRONIZATION_KEY);
		if (jsonObject == null)
			return null;
		return new Synchronization(jsonObject);
	}

	public boolean getInstallReferrer() {
		return installReferrer;
	}

	public void setInstallReferrer(boolean installReferrer) {
		this.installReferrer = installReferrer;
	}

	public boolean getBrowser() {
		return browser;
	}

	public void setBrowser(boolean browser) {
		this.browser = browser;
	}

	public String getClickToken() {
		return clickToken;
	}

	public void setClickToken(String clickToken) {
		this.clickToken = clickToken;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("installReferrer", installReferrer);
			jsonObject.put("browser", browser);
			if (clickToken != null)
				jsonObject.put("clickToken", clickToken);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "installReferrer"))
				setInstallReferrer(jsonObject.getBoolean("installReferrer"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "browser"))
				setBrowser(jsonObject.getBoolean("browser"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clickToken"))
				setClickToken(jsonObject.getString("clickToken"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}


}
