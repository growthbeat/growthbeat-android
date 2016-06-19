package com.growthbeat.http;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.GrowthbeatException;

public class GrowthbeatHttpClient extends BaseHttpClient {

    public GrowthbeatHttpClient() {
        super();
    }

    public GrowthbeatHttpClient(String baseUrl, int connectTimeout, int readTimeout) {
        super(baseUrl, connectTimeout, readTimeout);
    }

    public JSONObject get(String api, Map<String, Object> params) {
        return request("GET", api, params);
    }

    public JSONObject get(String api, Map<String, Object> params, String userAgent) {
        return request("GET", api, params, userAgent);
    }

    public JSONObject post(String api, Map<String, Object> params) {
        return request("POST", api, params);
    }

    public JSONObject post(String api, Map<String, Object> params, String userAgent) {
        return request("POST", api, params, userAgent);
    }

    public JSONObject put(String api, Map<String, Object> params) {
        return request("PUT", api, params);
    }

    public JSONObject put(String api, Map<String, Object> params, String userAgent) {
        return request("PUT", api, params, userAgent);
    }

    public JSONObject delete(String api, Map<String, Object> params) {
        return request("DELETE", api, params);
    }

    public JSONObject delete(String api, Map<String, Object> params, String userAgent) {
        return request("DELETE", api, params, userAgent);
    }

    protected JSONObject request(String method, String api, Map<String, Object> params) {
        String response = super.request(RequestMethod.valueOf(method), api, params);
        return fetchJSONObject(response);
    }

    protected JSONObject request(String method, String api, Map<String, Object> params, String userAgent) {
        String response = super.request(RequestMethod.valueOf(method), api, params, userAgent);
        return fetchJSONObject(response);
    }

    private JSONObject fetchJSONObject(String response) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            throw new GrowthbeatException("Failed to parse response JSON. " + e.getMessage(), e);
        }

        return jsonObject;

    }

    public JSONArray getForArray(String api, Map<String, Object> params) {
        return requestForArray("GET", api, params);
    }

    public JSONArray getForArray(String api, Map<String, Object> params, String userAgent) {
        return requestForArray("GET", api, params, userAgent);
    }

    public JSONArray postForArray(String api, Map<String, Object> params) {
        return requestForArray("POST", api, params);
    }

    public JSONArray postForArray(String api, Map<String, Object> params, String userAgent) {
        return requestForArray("POST", api, params, userAgent);
    }

    public JSONArray putForArray(String api, Map<String, Object> params) {
        return requestForArray("PUT", api, params);
    }

    public JSONArray putForArray(String api, Map<String, Object> params, String userAgent) {
        return requestForArray("PUT", api, params, userAgent);
    }

    public JSONArray deleteForArray(String api, Map<String, Object> params) {
        return requestForArray("DELETE", api, params);
    }

    public JSONArray deleteForArray(String api, Map<String, Object> params, String userAgent) {
        return requestForArray("DELETE", api, params, userAgent);
    }

    protected JSONArray requestForArray(String method, String api, Map<String, Object> params) {
        String response = super.request(RequestMethod.valueOf(method), api, params);
        return fetchJSONArray(response);
    }

    protected JSONArray requestForArray(String method, String api, Map<String, Object> params, String userAgent) {
        String response = super.request(RequestMethod.valueOf(method), api, params, userAgent);
        return fetchJSONArray(response);
    }

    private JSONArray fetchJSONArray(String response) {

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
        } catch (JSONException e) {
            throw new GrowthbeatException("Failed to parse response JSON. " + e.getMessage(), e);
        }

        return jsonArray;

    }

}
