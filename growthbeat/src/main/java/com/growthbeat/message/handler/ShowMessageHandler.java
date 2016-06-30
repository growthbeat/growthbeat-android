package com.growthbeat.message.handler;

/**
 * Created by tabatakatsutoshi on 2016/06/17.
 */
public interface ShowMessageHandler {

	void complete(MessageRenderHandler run);

	void error(String error);

	public static interface MessageRenderHandler {

		abstract void render();

	}

}
