package com.growthbeat;

public abstract class CatchableThread extends Thread {

    public CatchableThread() {
        super();
        initializeUncaughtExceptionHandler();
    }

    public CatchableThread(Runnable runnable) {
        super(runnable);
        initializeUncaughtExceptionHandler();
    }

    public CatchableThread(Runnable runnable, String threadName) {
        super(runnable, threadName);
        initializeUncaughtExceptionHandler();
    }

    private void initializeUncaughtExceptionHandler() {

        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                CatchableThread.this.uncaughtException(thread, e);
            }
        });

    }

    public abstract void uncaughtException(Thread thread, Throwable e);

}
