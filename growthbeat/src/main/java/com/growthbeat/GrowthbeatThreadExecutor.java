package com.growthbeat;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shotaro Watanabe on 2015/10/27.
 */
public class GrowthbeatThreadExecutor extends ThreadPoolExecutor {
    private static final String TAG = "Growthbeat";
    private static final String THREAD_NAME = "growthbeat-thread";
    private static final int DEFAULT_THREAD_COUNT = 3;

    public GrowthbeatThreadExecutor() {
        this(DEFAULT_THREAD_COUNT);
    }

    public GrowthbeatThreadExecutor(int poolSize) {
        super(poolSize, poolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
            new GrowthbeatThreadFactory());
    }

    private static class GrowthbeatThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new GrowthbeatThread(r);
        }
    }

    private static class GrowthbeatThread extends CatchableThread {

        public GrowthbeatThread(Runnable runnable) {
            super(runnable, THREAD_NAME);
        }

        @Override
        public void uncaughtException(java.lang.Thread thread, Throwable e) {
            String message = "Uncaught Exception: " + e.getClass().getName();
            if (e.getMessage() != null)
                message += "; " + e.getMessage();
            Log.w(TAG, message);
            e.printStackTrace();
        }

    }

}
