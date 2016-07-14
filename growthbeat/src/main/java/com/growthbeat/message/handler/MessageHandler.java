package com.growthbeat.message.handler;

import com.growthbeat.message.model.Message;

public interface MessageHandler {

    boolean handle(final Message message, MessageDonwloadHandler donwloadMessageHandler);

    public static interface MessageDonwloadHandler {

        void complete(ShowMessageHandler.MessageRenderHandler renderHandler);

    }

}
