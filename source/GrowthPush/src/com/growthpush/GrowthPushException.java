package com.growthpush;

import com.growthbeat.GrowthbeatException;

public class GrowthPushException extends GrowthbeatException {

	private static final long serialVersionUID = 1L;

	public GrowthPushException() {
		super();
	}

	public GrowthPushException(String message) {
		super(message);
	}

	public GrowthPushException(Throwable throwable) {
		super(throwable);
	}

	public GrowthPushException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
