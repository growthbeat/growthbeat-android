package com.growthbeat;

public class GrowthbeatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code = -1;

    public GrowthbeatException() {
        super();
    }

    public GrowthbeatException(String message) {
        super(message);
    }

    public GrowthbeatException(String message, int code) {
        super(message);
        setCode(code);
    }

    public GrowthbeatException(Throwable throwable) {
        super(throwable);
    }

    public GrowthbeatException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
