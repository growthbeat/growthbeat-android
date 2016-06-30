package com.growthpush.bridge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

public abstract class ExternalFrameworkBridge {

	protected List<String> customFileds = new ArrayList<String>();

	public void callbackWithStoredCustomFiled() {
		if (!customFileds.isEmpty()) {
			String message = customFileds.get(0);
			callbackExternalFramework(message);
			customFileds.remove(0);
		}
	}

	public void callbackExternalFrameworkWithExtra(Bundle bundle) {
		String message = serializeCustomFiled(bundle);
		callbackExternalFramework(message);
	}

	protected abstract void callbackExternalFramework(final String customFiled);

	private String serializeCustomFiled(Bundle bundle) {

		JSONObject json = new JSONObject();
		for (String key : bundle.keySet()) {

			String value = bundle.get(key).toString();
			if (key.equals("showDialog") || key.equals("collapse_key") || key.equals("from"))
				continue;
			try {
				if (key.equals("growthpush"))
					json.put(key, new JSONObject(value));
				else
					json.put(key, value);
			} catch (JSONException e) {
			}
		}

		return json.toString() != null ? json.toString() : null;

	}

}
