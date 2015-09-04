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

	private boolean cookieTracking;
	private boolean installReferrer;
	private boolean deviceFingerprint;
	private String clickId;
	
	private boolean handled;

	protected Synchronization() {
		super();
	}

	protected Synchronization(JSONObject jsonObject) {
		super(jsonObject);
		
	}

	public static Synchronization synchronize(String applicationId, String version, String credentialId, String fingerprintParameters) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (applicationId != null)
			params.put("applicationId", applicationId);
		params.put("os", "android");
		if (version != null)
			params.put("version", version);
		if (credentialId != null)
			params.put("credentialId", credentialId);
		if (fingerprintParameters != null)
			params.put("fingerprintParameters", fingerprintParameters);
		JSONObject jsonObject = GrowthLink.getInstance().getHttpClient().post("2/synchronize", params);

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
	
	public static void handleFinish(){
		Synchronization synchronization = Synchronization.load();
		synchronization.setHandled(true);
		Synchronization.save(synchronization);
	}
	
	public boolean getInstallReferrer() {
		return installReferrer;
	}

	public void setInstallReferrer(boolean installReferrer) {
		this.installReferrer = installReferrer;
	}

	public boolean getCookieTracking() {
		return cookieTracking;
	}

	public void setCookieTracking(boolean cookieTracking) {
		this.cookieTracking = cookieTracking;
	}
	
	public boolean getDeviceFingerprint(){
		return this.deviceFingerprint;
	}
	
	public void setDeviceFingerprint(boolean deviceFingerprint){
		this.deviceFingerprint = deviceFingerprint;
	}

	public String getClickId() {
		return clickId;
	}

	public void setClickId(String clickId) {
		this.clickId = clickId;
	}
	
	public boolean getHandled() {
		return this.handled;
	}
	
	public void setHandled(boolean handled) {
		this.handled = handled;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("installReferrer", installReferrer);
			jsonObject.put("cookieTracking", cookieTracking);
			jsonObject.put("deviceFingerprint", deviceFingerprint);
			jsonObject.put("handled", handled);
			if (clickId != null)
				jsonObject.put("clickId", clickId);
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "cookieTracking"))
				setCookieTracking(jsonObject.getBoolean("cookieTracking"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "deviceFingerprint"))
				setDeviceFingerprint(jsonObject.getBoolean("deviceFingerprint"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "handled")) {
				setHandled(jsonObject.getBoolean("handled"));
			} else {
				setHandled(false);
			}
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clickId"))
				setClickId(jsonObject.getString("clickId"));
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}


}
