package com.growthbeat.message.handler;

import com.growthbeat.message.MessageQueue;

public interface MessageHandler {

	boolean handle(MessageQueue messageJob);

}
