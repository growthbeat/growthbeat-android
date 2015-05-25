package com.growthbeat.analytics;

import java.util.Map;

public interface EventHandler {

	void callback(String eventId, Map<String, String> properties);

}
