package com.growthbeat.message.handler;


import com.growthbeat.message.model.Message;

/**
 * Created by tabatakatsutoshi on 2016/06/17.
 */
public interface ShowMessageHandler {
    void onComplete(Message message);

    void onError();
}
