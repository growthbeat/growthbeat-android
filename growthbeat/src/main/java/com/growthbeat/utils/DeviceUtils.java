package com.growthbeat.utils;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.growthbeat.Growthbeat;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public final class DeviceUtils {

	public static Future<Boolean> getTrackingEnabled() {
		FutureTask<Boolean> future = new FutureTask<Boolean>(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(Growthbeat.getInstance().getContext());
					return !adInfo.isLimitAdTrackingEnabled();
				} catch (Throwable e) {
					return null;
				}
			}
		});
		Growthbeat.getInstance().getExecutor().execute(future);
		return future;
	}

	public static Future<String> getAdvertisingId() {
		FutureTask<String> future = new FutureTask<String>(new Callable<String>() {
			public String call() throws Exception {
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(Growthbeat.getInstance().getContext());
					return adInfo.getId();
				} catch (Throwable e) {
					return null;
				}
			}
		});
		Growthbeat.getInstance().getExecutor().execute(future);
		return future;
	}

	public static String getModel() {
		return Build.MODEL;
	}

	public static String getDevice() {
		return Build.DEVICE;
	}

	public static String getOsVersion() {
		return Build.VERSION.RELEASE;
	}

	public static int getApiVersion() {
		return Build.VERSION.SDK_INT;
	}

	public static String getCountry() {
		if (Locale.getDefault() == null)
			return null;
		return Locale.getDefault().getCountry();
	}

	public static String getLanguage() {
		if (Locale.getDefault() == null)
			return null;
		return Locale.getDefault().getLanguage();
	}

	public static String getTimeZone() {
		if (Locale.getDefault() == null)
			return null;
		return TimeZone.getDefault().getID();
	}

	public static Integer getTimeZoneOffset() {
		if (TimeZone.getDefault() == null)
			return null;
		return TimeZone.getDefault().getRawOffset() / (60 * 60 * 1000);
	}

	public static boolean connectedToWiFi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static long getAvailableMemory(Context context) {
		ActivityManager activityManager = SystemServiceUtils.getActivityManager(context);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.availMem;
	}

	public static boolean isLowMemory(Context context) {
		ActivityManager activityManager = SystemServiceUtils.getActivityManager(context);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		return memoryInfo.lowMemory;
	}

	@SuppressWarnings("deprecation")
	public static Point getDisplaySize(Context context) {
		WindowManager windowManager = SystemServiceUtils.getWindowManager(context);
		Point point = new Point();
		Display display = windowManager.getDefaultDisplay();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
			display.getSize(point);
		else {
			point.x = display.getWidth();
			point.y = display.getHeight();
		}

		return point;
	}

}
