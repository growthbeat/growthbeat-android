package com.growthbeat.message;

import android.os.Parcel;
import android.os.Parcelable;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.message.model.Message;
import com.growthbeat.utils.JSONObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a13048 on 16/06/23.
 */
public class MessageQueue {

    public static final Parcelable.Creator<MessageQueue> CREATOR = new Parcelable.Creator<MessageQueue>() {

        @Override
        public MessageQueue[] newArray(int size) {
            return new MessageQueue[size];
        }

        @Override
        public MessageQueue createFromParcel(Parcel source) {

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(source.readString());
            } catch (JSONException e) {
                throw new GrowthbeatException("Failed to parse JSON. " + e.getMessage(), e);
            }

            return new MessageQueue(jsonObject);

        }
    };
    private String uuid;
    private Message message;

    public MessageQueue() {
    }

    public MessageQueue(String uuid, Message message) {
        setUuid(uuid);
        setMessage(message);
    }
    public MessageQueue(JSONObject jsonObject) {

        try {
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "uuid"))
                setUuid(jsonObject.getString("uuid"));
            if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "message"))
                setMessage(Message.getFromJsonObject(jsonObject.getJSONObject("message")));
        } catch (JSONException e) {
        }

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
