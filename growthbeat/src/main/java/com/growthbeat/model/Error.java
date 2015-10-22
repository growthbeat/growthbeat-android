package com.growthbeat.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.utils.JSONObjectUtils;

public class Error extends Model {

    private Integer status;
    private Integer code;
    private String message;

    public Error() {
        super();
    }

    public Error(JSONObject jsonObject) {
        this();
        setJsonObject(jsonObject);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            if (status != null)
                jsonObject.put("status", status);
            if (code != null)
                jsonObject.put("code", code);
            if (message != null)
                jsonObject.put("message", message);
        } catch (JSONException e) {
            return null;
        }

        return jsonObject;

    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "status"))
                setStatus(jsonObject.getInt("status"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "code"))
                setCode(jsonObject.getInt("code"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "message"))
                setMessage(jsonObject.getString("message"));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.");
        }

    }

}
