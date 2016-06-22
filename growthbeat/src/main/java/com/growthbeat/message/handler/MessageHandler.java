package com.growthbeat.message.handler;

import com.growthbeat.message.model.Message;

public interface MessageHandler {

	boolean handle(Message message);

}
