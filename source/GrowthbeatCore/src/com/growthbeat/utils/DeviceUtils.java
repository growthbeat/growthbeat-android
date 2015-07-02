package com.growthbeat.utils;

import java.util.Locale;
import java.util.TimeZone;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.growthbeat.GrowthbeatCore;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

public final class DeviceUtils {
	
	public interface AdvertisingCallback{
		
		void onAdvertisingIdGet(String advertisingId);
		
	}
	
	public static void getAdvertisingId(final AdvertisingCallback callback, final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final StringBuffer advertisingIdBuffer = new StringBuffer();
				try {
					Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(GrowthbeatCore.getInstance().getContext());
					if (adInfo.getId() != null && !adInfo.isLimitAdTrackingEnabled()){
						advertisingIdBuffer.append(adInfo.getId());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (handler != null){
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							callback.onAdvertisingIdGet(advertisingIdBuffer.toString());
						}
						
					});
				} else {
					callback.onAdvertisingIdGet(advertisingIdBuffer.toString());
				}
				
				
			}
		}).start();
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
		try {
			ConnectivityManager connectivityManager = SystemServiceUtils.getConnectivityManager(context);
			State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if (wifi == State.CONNECTED || wifi == State.CONNECTING)
				return true;
		} catch (Exception e) {
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
