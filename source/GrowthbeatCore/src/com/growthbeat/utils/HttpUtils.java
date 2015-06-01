package com.growthbeat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public final class HttpUtils {

	public static final List<NameValuePair> makeNameValuePairs(Map<String, Object> parameters) {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		for (Map.Entry<String, Object> entry : parameters.entrySet())
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));

		return nameValuePairs;

	}

}
