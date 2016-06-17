package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;
import com.growthpush.model.Event;

public class Task extends Model {

    public static enum MessageOrientation {
        vertical, horizontal
    }

    private String id;
    private String applicationId;
    private String goalId;
    private String segmentId;
    private MessageOrientation orientation;
    private Date begin;
    private Date end;
    private int capacity;
    private Date created;



    public Task() {
        super();
    }

    public Task(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static List<Task> getTasks(String applicationId, String credentialId, int goalId) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (applicationId != null)
            params.put("applicationId", applicationId);
        if (credentialId != null)
            params.put("credentialId", credentialId);

        params.put("goalId", goalId);

        JSONArray jsonArray = GrowthPush.getInstance().getHttpClient().postForArray("/1/tasks", params);
        return createList(jsonArray);
    }

    public static List<Task> createList(JSONArray jsonArray) {
        List<Task> tasks = new ArrayList<Task>();
        if (jsonArray == null) {
            return tasks;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject row = null;
            try {
                row = jsonArray.getJSONObject(i);
                tasks.add(new Task(row));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tasks;
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

    public String getName() {
        return goalId;
    }

    public void setName(String name) {
        this.goalId = name;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public MessageOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(MessageOrientation orientation) {
        this.orientation = orientation;
    }


    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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
            if (applicationId != null)
                jsonObject.put("applicationId", applicationId);
            if (goalId != null)
                jsonObject.put("goalId", goalId);
            if (segmentId != null)
                jsonObject.put("segmentId", segmentId);
            if (orientation != null) {
                jsonObject.put("orientation", orientation.toString());
            }
            if (begin != null)
                jsonObject.put("begin", DateUtils.formatToDateTimeString(begin));
            if (end != null)
                jsonObject.put("end", DateUtils.formatToDateTimeString(end));
            jsonObject.put("capacity", capacity);
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get JSON.");
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "applicationId"))
                setApplicationId(jsonObject.getString("applicationId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "goalId"))
                setName(jsonObject.getString("goalId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "segmentId"))
                setSegmentId(jsonObject.getString("segmentId"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "orientation"))
                setOrientation(MessageOrientation.valueOf(jsonObject.getString("orientation")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "begin"))
                setBegin(DateUtils.parseFromDateTimeString(jsonObject.getString("begin")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "end"))
                setEnd(DateUtils.parseFromDateTimeString(jsonObject.getString("end")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "capacity"))
                setCapacity(jsonObject.getInt("capacity"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
