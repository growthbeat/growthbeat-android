package com.growthbeat.message.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.model.Intent;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Button extends Model {

    public enum ButtonType {
        plain, image, screen, close
    }
    private ButtonType type;
    private Date created;
    private Message message;
    private Intent intent;

    protected Button() {
        super();
    }

    protected Button(JSONObject jsonObject) {
        super(jsonObject);
    }

    public static Button getFromJsonObject(JSONObject jsonObject) {

        Button button = new Button(jsonObject);
        switch (button.getType()) {
            case plain:
                return new PlainButton(jsonObject);
            case image:
                return new ImageButton(jsonObject);
            case close:
                return new CloseButton(jsonObject);
            case screen:
                return new ScreenButton(jsonObject);
            default:
                return null;
        }

    }

    public ButtonType getType() {
        return type;
    }

    public void setType(ButtonType type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    @Override
    public JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();

        try {
            if (type != null)
                jsonObject.put("type", type.toString());
            if (message != null)
                jsonObject.put("message", message.getJsonObject());
            if (intent != null)
                jsonObject.put("intent", intent.getJsonObject());
            if (created != null)
                jsonObject.put("created", DateUtils.formatToDateTimeString(created));
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
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "type"))
                setType(ButtonType.valueOf(jsonObject.getString("type")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "message"))
                setMessage(Message.getFromJsonObject(jsonObject.getJSONObject("message")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "intent"))
                setIntent(Intent.getFromJsonObject(jsonObject.getJSONObject("intent")));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
                setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to parse JSON.", e);
        }

    }

}
