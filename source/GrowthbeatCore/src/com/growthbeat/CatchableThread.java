package com.growthbeat;

public abstract class CatchableThread extends java.lang.Thread {

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

		setUncaughtExceptionHandler(new java.lang.Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(java.lang.Thread thread, Throwable e) {
				CatchableThread.this.uncaughtException(thread, e);
			}
		});

	}

	public abstract void uncaughtException(java.lang.Thread thread, Throwable e);

}
