package com.growthbeat.message.model;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.model.Model;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a13048 on 2017/03/10.
 */

public class ShowMessageCount extends Model {

    private String clientId;
    private String messageId;
    private String taskId;
    private int count;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ShowMessageCount(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static ShowMessageCount receiveCount(String clientId, String applicationId, String credentialId, String taskId, String messageId) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (clientId != null)
            params.put("clientId", clientId);
        if (applicationId != null)
            params.put("applicationId", applicationId);
        if (credentialId != null)
            params.put("credentialId", credentialId);
        if (taskId != null)
            params.put("taskId", taskId);
        if (messageId != null)
            params.put("messageId", messageId);

        JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().post("4/receive/count", params);
        if (jsonObject == null)
            throw new GrowthbeatException("Failed to count up message.");

        return new ShowMessageCount(jsonObject);

    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("clientId", getClientId());
            jsonObject.put("messageId", getMessageId());
            jsonObject.put("taskId", getTaskId());
            jsonObject.put("count", getCount());
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to get JSON.", e);
        }

        return null;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "clientId"))
                setClientId(jsonObject.getString("clientId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "messageId"))
                setMessageId(jsonObject.getString("messageId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "taskId"))
                setTaskId(jsonObject.getString("taskId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "count"))
                setCount(jsonObject.getInt("count"));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }
    }

}
