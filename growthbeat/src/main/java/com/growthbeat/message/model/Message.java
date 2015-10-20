package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.message.GrowthMessage;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;

public class Message extends Model implements Parcelable {

	private String id;
	private int version;
	private Type type;
	private String eventId;
	private int frequency;
	private String segmentId;
	private int cap;
	private Animation animation;
	private Date created;
	private Task task;
	private List<Button> buttons;

	protected Message() {
		super();
	}

	protected Message(JSONObject jsonObject) {
		super(jsonObject);
	}

	public static Message getFromJsonObject(JSONObject jsonObject) {

		Message message = new Message(jsonObject);
		switch (message.getType()) {
		case plain:
			return new PlainMessage(jsonObject);
		case image:
			return new ImageMessage(jsonObject);
		case banner:
			return new BannerMessage(jsonObject);
		case swipe:
			return new SwipeMessage(jsonObject);
		default:
			return null;
		}

	}

	public static Message receive(String clientId, String eventId, String credentialId) {

		Map<String, Object> params = new HashMap<String, Object>();
		if (clientId != null)
			params.put("clientId", clientId);
		if (eventId != null)
			params.put("eventId", eventId);
		if (credentialId != null)
			params.put("credentialId", credentialId);

		JSONObject jsonObject = GrowthMessage.getInstance().getHttpClient().post("1/receive", params);
		if (jsonObject == null)
			return null;

		return Message.getFromJsonObject(jsonObject);

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(String segmentId) {
		this.segmentId = segmentId;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public int getCap() {
		return cap;
	}

	public void setCap(int cap) {
		this.cap = cap;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	@Override
	public JSONObject getJsonObject() {

		JSONObject jsonObject = new JSONObject();

		try {
			if (id != null)
				jsonObject.put("id", id);
			jsonObject.put("version", version);
			if (type != null)
				jsonObject.put("type", type.toString());
			if (eventId != null)
				jsonObject.put("eventId", eventId);
			jsonObject.put("frequency", frequency);
			if (segmentId != null)
				jsonObject.put("segmentId", segmentId);
			jsonObject.put("cap", cap);
			if (created != null)
				jsonObject.put("created", DateUtils.formatToDateTimeString(created));
			if (task != null)
				jsonObject.put("task", task.getJsonObject());
			if (buttons != null) {
				JSONArray buttonJsonArray = new JSONArray();
				for (Button button : buttons)
					buttonJsonArray.put(button.getJsonObject());
				jsonObject.put("buttons", buttonJsonArray);
			}
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "id"))
				setId(jsonObject.getString("id"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "version"))
				setVersion(jsonObject.getInt("version"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "type"))
				setType(Type.valueOf(jsonObject.getString("type")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "eventId"))
				setEventId(jsonObject.getString("eventId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "frequency"))
				setFrequency(jsonObject.getInt("frequency"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "segmentId"))
				setSegmentId(jsonObject.getString("segmentId"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "cap"))
				setCap(jsonObject.getInt("cap"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "animation"))
				setAnimation(Animation.valueOf(jsonObject.getString("animation")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
				setCreated(DateUtils.parseFromDateTimeString(jsonObject.getString("created")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "task"))
				setTask(new Task(jsonObject.getJSONObject("task")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "buttons")) {
				List<Button> buttons = new ArrayList<Button>();
				JSONArray buttonJsonArray = jsonObject.getJSONArray("buttons");
				for (int i = 0; i < buttonJsonArray.length(); i++)
					buttons.add(Button.getFromJsonObject(buttonJsonArray.getJSONObject(i)));
				setButtons(buttons);
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

	public static enum Type {
		plain, image, banner, swipe
	}

	public static enum Animation {
		none, defaults
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getJsonObject().toString());
	}

	public static final Creator<Message> CREATOR = new Creator<Message>() {

		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}

		@Override
		public Message createFromParcel(Parcel source) {

			JSONObject jsonObject = null;

			try {
				jsonObject = new JSONObject(source.readString());
			} catch (JSONException e) {
				throw new GrowthbeatException("Failed to parse JSON. " + e.getMessage(), e);
			}

			return Message.getFromJsonObject(jsonObject);

		}
	};

}
