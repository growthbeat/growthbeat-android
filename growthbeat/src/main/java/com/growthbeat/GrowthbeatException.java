package com.growthbeat;

public class GrowthbeatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GrowthbeatException() {
        super();
    }

    public GrowthbeatException(String message) {
        super(message);
    }

    public GrowthbeatException(Throwable throwable) {
        super(throwable);
    }

    public GrowthbeatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
