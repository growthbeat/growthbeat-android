package com.growthbeat.message.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.growthbeat.GrowthbeatException;
import com.growthbeat.model.Model;
import com.growthbeat.utils.DateUtils;
import com.growthbeat.utils.JSONObjectUtils;
import com.growthpush.GrowthPush;
import com.growthpush.model.Tag;

import android.os.Parcel;
import android.os.Parcelable;

public class Message extends Model implements Parcelable {

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
	private String id;
	private Task task;
	private MessageType type;
	private Background background;
	private Date created;
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
		case card:
			return new CardMessage(jsonObject);
		case swipe:
			return new SwipeMessage(jsonObject);
		default:
			return null;
		}

	}

	public static Message receive(String taskId, String applicationId, String clientId, String credentialId) {
		Map<String, Object> params = new HashMap<String, Object>();

		if (taskId != null)
			params.put("taskId", taskId);
		if (applicationId != null)
			params.put("applicationId", applicationId);
		if (clientId != null)
			params.put("clientId", clientId);
		if (credentialId != null)
			params.put("credentialId", credentialId);

		JSONObject jsonObject = GrowthPush.getInstance().getHttpClient().get("4/receive", params);
		if (jsonObject == null)
			return null;

		return Message.getFromJsonObject(jsonObject);
	}

	public static int receiveCount(String clientId, String applicationId, String credentialId, String taskId, String messageId) {

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

		Tag tag = new Tag(jsonObject);

		try {
			return Integer.valueOf(tag.getValue());
		} catch (NumberFormatException e) {
			return 0;
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
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
			if (type != null)
				jsonObject.put("type", type.toString());
			if (background != null)
				jsonObject.put("background", background.getJsonObject());
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
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "type"))
				setType(MessageType.valueOf(jsonObject.getString("type")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "background"))
				setBackground(new Background(jsonObject.getJSONObject("background")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "created"))
				setCreated(DateUtils.parseFromDateTimeStringWithFormat(jsonObject.getString("created"), "yyyy-MM-dd HH:mm:ss"));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "task"))
				setTask(new Task(jsonObject.getJSONObject("task")));
			if (JSONObjectUtils.hasAndIsNotNull(jsonObject, "buttons")) {
				List<Button> buttons = new ArrayList<>();
				JSONArray buttonJsonArray = jsonObject.getJSONArray("buttons");
				for (int i = 0; i < buttonJsonArray.length(); i++)
					buttons.add(Button.getFromJsonObject(buttonJsonArray.getJSONObject(i)));
				setButtons(buttons);
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException("Failed to parse JSON.", e);
		}

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getJsonObject().toString());
	}

	public enum MessageType {
		plain,
		card,
		swipe
	}

}
