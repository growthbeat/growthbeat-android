package com.growthbeat.message.handler;

import com.growthbeat.message.model.Message;

public interface MessageHandler {

    public boolean handle(final Message message, ShowMessageHandler showMessageHandler);

}
